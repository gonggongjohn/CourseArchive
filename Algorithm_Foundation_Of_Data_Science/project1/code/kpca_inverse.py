from sklearn.decomposition import KernelPCA
import numpy as np
import cv2


def evaluate(mat_origin, mat_comp):
    mse = np.mean(np.square(mat_origin - mat_comp))
    psnr = 10 * np.log10(255 * 255 / mse)
    print('MSE:', mse)
    print('PSNR:', psnr)
    return mse, psnr


comp_n = 50  # Number of principle components
gamma = 2
i = 0
img = cv2.imread('images/agricultural/agricultural{0:0>2d}.tif'.format(i))
b, g, r = cv2.split(img)
model = KernelPCA(n_components=comp_n, kernel='rbf', fit_inverse_transform=True, gamma=gamma, alpha=5e-3)
reduced_b = model.fit_transform(b)
comp_b = model.inverse_transform(reduced_b)
reduced_g = model.fit_transform(g)
comp_g = model.inverse_transform(reduced_g)
reduced_r = model.fit_transform(r)
comp_r = model.inverse_transform(reduced_r)
img_compressed = cv2.merge([comp_b, comp_g, comp_r])
mse, psnr = evaluate(img, img_compressed)
cv2.imwrite('kpca.jpg', img_compressed)
