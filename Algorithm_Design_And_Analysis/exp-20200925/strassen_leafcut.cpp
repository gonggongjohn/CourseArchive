#include<iostream>
#include<fstream>
#include<cmath>
#include<cstdlib>
#include<ctime>
using namespace std;
struct Matrix {
    int row, column;
    int** m;

    Matrix(int r, int c) {
        row = r;
        column = c;
        m = (int**) malloc(sizeof(int*)*r);
        for (int i = 0; i < r; i++)
            m[i] = (int*)malloc(sizeof(int) * c);
    }

    Matrix(const Matrix& mat) {
        row = mat.row;
        column = mat.column;
        m = (int**)malloc(sizeof(int*) * mat.row);
        for (int i = 0; i < mat.row; i++)
            m[i] = (int*)malloc(sizeof(int) * mat.column);
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++)
                m[i][j] = mat.m[i][j];
    }

    Matrix& operator = (const Matrix& mat) {
        if (this != &mat) {
            row = mat.row;
            column = mat.column;
            m = (int**)malloc(sizeof(int*) * mat.row);
            for (int i = 0; i < mat.row; i++)
                m[i] = (int*)malloc(sizeof(int) * mat.column);
            for (int i = 0; i < row; i++)
                for (int j = 0; j < column; j++)
                    m[i][j] = mat.m[i][j];
        }
        return *this;
    }

    ~Matrix() {
        if (m != NULL) {
            for (int i = 0; i < row; i++) {
                delete[] m[i];
            }
            delete[] m;
        }
    }

};

int tm1[2048 * 2048], tm2[2048 * 2048], cntp;

Matrix matAdd(Matrix* matA, Matrix* matB) {
    Matrix matR = Matrix((*matA).row, (*matA).column);
    for (int i = 0; i < (*matA).row; i++)
        for (int j = 0; j < (*matA).column; j++) {
            matR.m[i][j] = (*matA).m[i][j] + (*matB).m[i][j];
        }
    return matR;
}
Matrix matSub(Matrix* matA, Matrix* matB) {
    Matrix matR = Matrix((*matA).row, (*matA).column);
    for (int i = 0; i < (*matA).row; i++)
        for (int j = 0; j < (*matA).column; j++) {
            matR.m[i][j] = (*matA).m[i][j] - (*matB).m[i][j];
        }
    return matR;
}
Matrix matSplit(Matrix mat, int rowStart, int columnStart, int rowEnd, int columnEnd) {
    Matrix matR = Matrix(rowEnd - rowStart + 1, columnEnd - columnStart + 1);
    for (int i = 0; i <= rowEnd - rowStart; i++)
        for (int j = 0; j <= columnEnd - columnStart; j++) {
            matR.m[i][j] = mat.m[rowStart + i][columnStart + j];
        }
    return matR;
}
Matrix matCombine(Matrix mat1, Matrix mat2, Matrix mat3, Matrix mat4) {
    Matrix matR = Matrix(mat1.row + mat3.row, mat1.column + mat2.column);
    for (int i = 0; i < mat1.row; i++)
        for (int j = 0; j < mat1.column; j++)
            matR.m[i][j] = mat1.m[i][j];
    for (int i = 0; i < mat2.row; i++)
        for (int j = 0; j < mat2.column; j++)
            matR.m[i][mat1.column + j] = mat2.m[i][j];
    for (int i = 0; i < mat3.row; i++)
        for (int j = 0; j < mat3.column; j++)
            matR.m[mat1.row + i][j] = mat3.m[i][j];
    for (int i = 0; i < mat4.row; i++)
        for (int j = 0; j < mat4.column; j++)
            matR.m[mat1.row + i][mat1.column + j] = mat4.m[i][j];
    return matR;
}
Matrix matProduct(Matrix* matA, Matrix* matB, int leafcut) {
    if ((*matA).row <= leafcut && (*matA).column <= leafcut && (*matB).row <= leafcut && (*matB).column <= leafcut) {
        Matrix matR = Matrix(leafcut, leafcut);
        int tsum;
        for(int i = 0; i < leafcut; i++) {
            for (int j = 0; j < leafcut; j++) {
                tsum = 0;
                for (int k = 0; k < leafcut; k++) {
                    matR.m[i][j] += (*matA).m[i][k] * (*matB).m[k][j];
                }
            }
        }
        return matR;
    }
    int midR = (*matA).row / 2 - 1;
    int midC = (*matA).column / 2 - 1;
    Matrix a11 = matSplit((*matA), 0, 0, midR, midC);
    Matrix a12 = matSplit((*matA), 0, midC + 1, midR, (*matA).column - 1);
    Matrix a21 = matSplit((*matA), midR + 1, 0, (*matA).row - 1, midC);
    Matrix a22 = matSplit((*matA), midR + 1, midC + 1, (*matA).row - 1, (*matA).column - 1);
    Matrix b11 = matSplit((*matB), 0, 0, midR, midC);
    Matrix b12 = matSplit((*matB), 0, midC + 1, midR, (*matA).column - 1);
    Matrix b21 = matSplit((*matB), midR + 1, 0, (*matA).row - 1, midC);
    Matrix b22 = matSplit((*matB), midR + 1, midC + 1, (*matA).row - 1, (*matA).column - 1);
    Matrix tmp1 = matAdd(&a11, &a22);
    Matrix tmp2 = matAdd(&b11, &b22);
    Matrix tmp3 = matAdd(&a21, &a22);
    Matrix tmp4 = matSub(&b12, &b22);
    Matrix tmp5 = matSub(&b21, &b11);
    Matrix tmp6 = matAdd(&a11, &a12);
    Matrix tmp7 = matSub(&a21, &a11);
    Matrix tmp8 = matAdd(&b11, &b12);
    Matrix tmp9 = matSub(&a12, &a22);
    Matrix tmp10 = matAdd(&b21, &b22);
    Matrix m1 = matProduct(&tmp1, &tmp2, leafcut);
    Matrix m2 = matProduct(&tmp3, &b11, leafcut);
    Matrix m3 = matProduct(&a11, &tmp4, leafcut);
    Matrix m4 = matProduct(&a22, &tmp5, leafcut);
    Matrix m5 = matProduct(&tmp6, &b22, leafcut);
    Matrix m6 = matProduct(&tmp7, &tmp8, leafcut);
    Matrix m7 = matProduct(&tmp9, &tmp10, leafcut);
    Matrix mtmp1 = matAdd(&m1, &m4);
    Matrix mtmp2 = matSub(&mtmp1, &m5);
    Matrix mtmp3 = matSub(&m1, &m2);
    Matrix mtmp4 = matAdd(&mtmp3, &m3);
    Matrix c11 = matAdd(&mtmp2, &m7);
    Matrix c12 = matAdd(&m3, &m5);
    Matrix c21 = matAdd(&m2, &m4);
    Matrix c22 = matAdd(&mtmp4, &m6);
    //cntp++;
    //cout << cntp << endl;
    return matCombine(c11, c12, c21, c22);
}
int main() {
    int cnt = 0, n;
    clock_t start, stop;
    ifstream fin1("data1.txt");
    ifstream fin2("data2.txt");
    while (!fin1.eof() && !fin2.eof()) {
        fin1 >> tm1[cnt];
        fin2 >> tm2[cnt];
        cnt++;
    }
    cnt--;
    n = sqrt(cnt);
    Matrix m1 = Matrix(n, n);
    Matrix m2 = Matrix(n, n);
    Matrix mr = Matrix(n, n);
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            m1.m[i][j] = tm1[i * n + j];
            m2.m[i][j] = tm2[i * n + j];
        }
    }
    cntp = 0;
    start = clock();
    mr = matProduct(&m1, &m2, 16);
    stop = clock();
    //for (int i = 0; i < n; i++) {
    //	for (int j = 0; j < n; j++) {
    //		cout << mr.m[i][j] << " ";
    //	}
    //	cout << endl;
    //}
    cout << "Time Cost: "<<(double)(stop - start) / CLOCKS_PER_SEC<<"s"<< endl;
    fin1.close();
    fin2.close();
    return 0;
}
