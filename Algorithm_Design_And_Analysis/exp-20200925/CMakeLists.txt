cmake_minimum_required(VERSION 3.17)
project(exp_20200925)

set(CMAKE_CXX_STANDARD 14)

add_executable(random_matrix random_matrix.cpp)
add_executable(matrix_product matrix_product.cpp)
add_executable(strassen strassen.cpp)
add_executable(strassen_leafcut strassen_leafcut.cpp)
add_executable(strassen_extend strassen_extend.cpp)