cmake_minimum_required(VERSION 3.17)
project(exp_20201127)

set(CMAKE_CXX_STANDARD 14)

add_executable(data_generator data_generator.cpp)
add_executable(flower_linear flower_linear.cpp)
add_executable(flower_hashmap flower_hashmap.cpp)
add_executable(flower_optimalBST flower_optimalBST.cpp)