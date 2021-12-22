import cv2
import numpy as np


def linear(self, x, y):
    return x.T @ y + self.c


def polynomial(self, x, y):
    return (self.alpha * (x.T @ y) + self.c) ** self.d_poly


def exp(self, x, y):
    return np.exp(- (1 / (2 * self.sigma ** 2)) * np.linalg.norm(x - y))


def rbf(x, y, gamma):
    return np.exp(- gamma * (np.linalg.norm(x - y) ** 2))


def anova(self, x, y):
    sum_a = 0
    for i in range(0, len(x)):
        term_1 = - self.sigma * ((x[i] - y[i]) ** 2)
        sum_a += np.exp(term_1) ** self.d_anova
    return sum_a


def sigmoid(self, x, y):
    return np.tanh(self.alpha * (x.T @ y) + self.c)


def compression_transform(mat, gamma, k, kernel):
    k_mat = []
    row, col = mat.shape
    for i in range(col):
        k_row = []
        for j in range(col):
            k_row.append(kernel(mat[:, i], mat[:, j], gamma))
        k_mat.append(k_row)
    k_mat = np.array(k_mat)
    ones = np.ones(k_mat.shape) / col
    k_mat = k_mat - ones @ k_mat - k_mat @ ones + ones @ k_mat @ ones
    eig_val, eig_vec = np.linalg.eig(k_mat)
    singular_list = [(np.sqrt(eig_val[i]), eig_vec[:, i] / np.sqrt(eig_val[i])) for i in range(len(eig_val))]
    singular_list.sort(key=lambda x: x[0], reverse=True)
    singular_list_k = singular_list[: k]
    sigma = np.diag([i[0] for i in singular_list_k])
    sigma = np.real_if_close(sigma, tol=1)
    v_mat = np.array([list(j[1]) for j in singular_list_k]).T
    v_mat = np.real_if_close(v_mat, tol=1)
    project_mat = sigma @ v_mat.T
    return project_mat


comp_n = 50  # Number of principle components
i = 0
gamma = 1e-6
img = cv2.imread('images/agricultural/agricultural{0:0>2d}.tif'.format(i), 0)
project_mat = compression_transform(img, gamma, comp_n, rbf)
print(project_mat)

