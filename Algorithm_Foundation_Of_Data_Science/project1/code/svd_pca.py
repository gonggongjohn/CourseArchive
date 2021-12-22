import cv2
import numpy as np
import time


def compression(mat, k):
    img_mean = np.mean(mat, axis=0)
    mat = mat - img_mean
    u, s, v = np.linalg.svd(mat)
    trans_mat = v.T[:, :k]
    compressed_mat = mat @ trans_mat @ trans_mat.T
    compressed_mat = compressed_mat + img_mean
    return compressed_mat


def evaluate(mat_origin, mat_comp):
    mse = np.mean(np.square(mat_origin - mat_comp))
    psnr = 10 * np.log10(255 * 255 / mse)
    print('MSE:', mse)
    print('PSNR:', psnr)


comp_n = 50  # Number of principle components
i = 0
img = cv2.imread('images/agricultural/agricultural{0:0>2d}.tif'.format(i))
b, g, r = cv2.split(img)
comp_b = compression(b, comp_n)
comp_g = compression(g, comp_n)
comp_r = compression(r, comp_n)
img_compressed = cv2.merge([comp_b, comp_g, comp_r])
evaluate(img, img_compressed)
cv2.imwrite('svd_pca.jpg', np.array(img_compressed, dtype='uint8'))
