cmake_minimum_required(VERSION 3.17)
project(exp_20200918)

set(CMAKE_CXX_STANDARD 14)

add_executable(A random_generator.cpp)
add_executable(B insertion_sort.cpp)
add_executable(C merge_sort.cpp)
add_executable(D bubble_sort.cpp)