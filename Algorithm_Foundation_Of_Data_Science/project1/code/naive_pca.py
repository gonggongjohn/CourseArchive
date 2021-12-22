import cv2
import numpy as np


def compression(mat, k):
    img_mean = np.mean(mat, axis=0)
    mat = mat - img_mean
    cov = mat @ mat.T
    eig_val, eig_vec = np.linalg.eig(cov)
    eig_val_index = np.argsort(-eig_val)
    trans_mat = []
    for i in range(k):
        trans_mat.append(eig_vec[:, eig_val_index[i]])
    trans_mat = np.array(trans_mat)
    compressed_mat = trans_mat.T @ trans_mat @ mat
    compressed_mat = compressed_mat + img_mean
    return compressed_mat


def evaluate(mat_origin, mat_comp):
    mse = np.mean(np.square(mat_origin - mat_comp))
    psnr = 10 * np.log10(255 * 255 / mse)
    print('MSE:', mse)
    print('PSNR:', psnr)
    return mse, psnr


comp_n = 50  # Number of principle components
i = 0
img = cv2.imread('images/agricultural/agricultural{0:0>2d}.tif'.format(i))
b, g, r = cv2.split(img)
comp_b = compression(b, comp_n)
comp_g = compression(g, comp_n)
comp_r = compression(r, comp_n)
img_compressed = cv2.merge([comp_b, comp_g, comp_r])
evaluate(img, img_compressed)
cv2.imwrite('naive_pca.jpg', np.array(img_compressed, dtype='uint8'))
