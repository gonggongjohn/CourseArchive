import numpy as np
from copy import deepcopy
import cv2


def update_weight(x, w, iteration, lr):
    y = np.dot(w, x)
    LT = np.tril(np.matmul(y[:, np.newaxis], y[np.newaxis, :]))
    w = w + lr / iteration * ((y[:, np.newaxis] * x) - (np.matmul(LT, w)))
    return w


def get_pca(mat, lr, k, max_iter, correct_mat):
    row, col = mat.shape
    wig = np.random.normal(0, 0.5, (k, row))
    wig_norm = wig / np.linalg.norm(wig, axis=1).reshape(k, 1)
    w_new = deepcopy(wig_norm)
    epoch = 1
    while epoch <= max_iter:
        for i in range(0, row):
            w_new = update_weight(mat[i], w_new, epoch, lr)
        print('Epoch: ', epoch, ', Loss: ', np.linalg.norm(w_new - correct_mat))
        print('PC1 Angle:', np.arccos(np.dot(w_new.T[:, 0], correct_mat.T[:, 0]) / (
                    np.linalg.norm(w_new.T[:, 0]) * (np.linalg.norm(correct_mat.T[:, 0])))))
        print('PC2 Angle:', np.arccos(np.dot(w_new.T[:, 1], correct_mat.T[:, 1]) / (
                    np.linalg.norm(w_new.T[:, 1]) * (np.linalg.norm(correct_mat.T[:, 1])))))
        epoch += 1
    return w_new


eta = 1e-6
max_epoch = 20000

i = 0
img = cv2.imread('images/agricultural/agricultural{0:0>2d}.tif'.format(i), 0)
img_mean = np.mean(img, axis=0)
img = img - img_mean

cov = img @ img.T
eig_val, eig_vec = np.linalg.eig(cov)
eig_val_index = np.argsort(-eig_val)
trans_mat = []
for i in range(2):
    trans_mat.append(eig_vec[:, eig_val_index[i]])
trans_mat = np.array(trans_mat)

w_new = get_pca(img, eta, 2, max_epoch, trans_mat)
print('Check if GHA 2 PCs are orthogonal: ')
print(np.dot(w_new[0], w_new[1]))
img_compressed = img @ w_new.T @ w_new
img_compressed = img_compressed + img_mean
cv2.imwrite('gha_pca.jpg', np.array(img_compressed, dtype='uint8'))
