import numpy as np
import cv2


def compresion_2d2dpca(images, k_row, k_col):
    size = images[0].shape
    mean_mat = np.zeros(size)
    for im in images:
        mean_mat = mean_mat + im
    mean_mat /= float(len(images))
    cov_row = np.zeros((size[1], size[1]))
    for im in images:
        diff_mat = im - mean_mat
        cov_row = cov_row + np.dot(diff_mat.T, diff_mat)
    cov_row /= float(len(images))
    eig_val_row, eig_vec_row = np.linalg.eig(cov_row)
    sorted_index = np.argsort(eig_val_row)
    x_k_mat = eig_vec_row[:, sorted_index[:-k_row - 1: -1]]
    cov_col = np.zeros((size[0], size[0]))
    for im in images:
        diff_mat = im - mean_mat
        cov_col += np.dot(diff_mat, diff_mat.T)
    cov_col /= float(len(images))
    eig_val_col, eig_vec_col = np.linalg.eig(cov_col)
    sorted_index = np.argsort(eig_val_col)
    z_k_mat = eig_vec_col[:, sorted_index[:-k_col - 1: -1]]
    return x_k_mat, z_k_mat


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

pc_row, pc_col = 50, 50
trans_b_x, trans_b_z = compresion_2d2dpca(samples_b, pc_row, pc_col)
trans_g_x, trans_g_z = compresion_2d2dpca(samples_g, pc_row, pc_col)
trans_r_x, trans_r_z = compresion_2d2dpca(samples_r, pc_row, pc_col)
i = 0
img_origin = cv2.imread('images/agricultural/agricultural{0:0>2d}.tif'.format(i))
res_b = trans_b_z @ trans_b_z.T @ samples_b[i] @ trans_b_x @ trans_b_x.T
res_g = trans_b_z @ trans_b_z.T @ samples_g[i] @ trans_b_x @ trans_b_x.T
res_r = trans_b_z @ trans_b_z.T @ samples_b[i] @ trans_b_x @ trans_b_x.T
res_b = np.array(res_b * 255.0, dtype='uint8')
res_g = np.array(res_g * 255.0, dtype='uint8')
res_r = np.array(res_r * 255.0, dtype='uint8')
img_compression = cv2.merge([res_b, res_g, res_r])
mse, psnr = evaluate(img_origin, img_compression)
cv2.imwrite('2d2dpca.jpg', img_compression)
