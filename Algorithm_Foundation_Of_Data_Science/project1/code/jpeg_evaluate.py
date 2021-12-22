import cv2
import numpy as np


def evaluate(mat_origin, mat_comp):
    mse = np.mean(np.square(mat_origin - mat_comp))
    psnr = 10 * np.log10(255 * 255 / mse)
    print('MSE:', mse)
    print('PSNR:', psnr)


img_origin = cv2.imread('images/agricultural/agricultural00.tif')
img_compressed = cv2.imread('jpeg_rst.jpg')
evaluate(img_origin, img_compressed)
