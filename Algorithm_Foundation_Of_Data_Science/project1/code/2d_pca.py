import numpy as np
import cv2


def compression_2dpca(images, k):
    size = images[0].shape
    mean_mat = np.zeros(size)
    for im in images:
        mean_mat = mean_mat + im
    mean_mat /= float(len(images))
    cov_row = np.zeros((size[1], size[1]))
    for s in images:
        diff_mat = s - mean_mat
        cov_row = cov_row + np.dot(diff_mat.T, diff_mat)
    cov_row /= float(len(images))
    eig_val, eig_vec = np.linalg.eig(cov_row)
    sorted_index = np.argsort(eig_val)
    x_k_mat = eig_vec[:, sorted_index[:-k - 1: -1]]
    return x_k_mat


def evaluate(mat_origin, mat_comp):
    mse = np.mean(np.square(mat_origin - mat_comp))
    psnr = 10 * np.log10(255 * 255 / mse)
    print('MSE:', mse)
    print('PSNR:', psnr)
    return mse, psnr


samples_b, samples_g, samples_r = [], [], []
for i in range(0, 100):
    img = cv2.imread('images/agricultural/agricultural{0:0>2d}.tif'.format(i))
    img_b, img_g, img_r = cv2.split(img)
    img_b_normed = np.empty(img_b.shape)
    img_g_normed = np.empty(img_g.shape)
    img_r_normed = np.empty(img_r.shape)
    for j in range(img_b.shape[0]):
        for k in range(img_b.shape[1]):
            img_b_normed[j, k] = img_b[j, k] / 255.0
            img_g_normed[j, k] = img_g[j, k] / 255.0
            img_r_normed[j, k] = img_r[j, k] / 255.0
    samples_b.append(img_b_normed)
    samples_g.append(img_g_normed)
    samples_r.append(img_r_normed)

pc = 50
trans_b = compression_2dpca(samples_b, pc)
trans_g = compression_2dpca(samples_g, pc)
trans_r = compression_2dpca(samples_r, pc)
i = 0
img_origin = cv2.imread('images/agricultural/agricultural{0:0>2d}.tif'.format(i))
Y_b = samples_b[0] @ trans_b @ trans_b.T
Y_g = samples_g[0] @ trans_g @ trans_g.T
Y_r = samples_r[0] @ trans_r @ trans_r.T
Y_b, Y_g, Y_r = Y_b * 255.0, Y_g * 255.0, Y_r * 255.0
Y_b = np.array(Y_b, dtype='uint8')
Y_g = np.array(Y_g, dtype='uint8')
Y_r = np.array(Y_r, dtype='uint8')
img_compressed = cv2.merge([Y_b, Y_g, Y_r])
mse, psnr = evaluate(img_origin, img_compressed)
cv2.imwrite('2dpca.jpg', img_compressed)
