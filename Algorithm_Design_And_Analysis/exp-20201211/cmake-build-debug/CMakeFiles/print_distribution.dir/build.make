# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.17

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Disable VCS-based implicit rules.
% : %,v


# Disable VCS-based implicit rules.
% : RCS/%


# Disable VCS-based implicit rules.
% : RCS/%,v


# Disable VCS-based implicit rules.
% : SCCS/s.%


# Disable VCS-based implicit rules.
% : s.%


.SUFFIXES: .hpux_make_needs_suffix_list


# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /Applications/CLion.app/Contents/bin/cmake/mac/bin/cmake

# The command to remove a file.
RM = /Applications/CLion.app/Contents/bin/cmake/mac/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211"

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211/cmake-build-debug"

# Include any dependencies generated for this target.
include CMakeFiles/print_distribution.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/print_distribution.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/print_distribution.dir/flags.make

CMakeFiles/print_distribution.dir/print_distribution.cpp.o: CMakeFiles/print_distribution.dir/flags.make
CMakeFiles/print_distribution.dir/print_distribution.cpp.o: ../print_distribution.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211/cmake-build-debug/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/print_distribution.dir/print_distribution.cpp.o"
	/Library/Developer/CommandLineTools/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/print_distribution.dir/print_distribution.cpp.o -c "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211/print_distribution.cpp"

CMakeFiles/print_distribution.dir/print_distribution.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/print_distribution.dir/print_distribution.cpp.i"
	/Library/Developer/CommandLineTools/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211/print_distribution.cpp" > CMakeFiles/print_distribution.dir/print_distribution.cpp.i

CMakeFiles/print_distribution.dir/print_distribution.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/print_distribution.dir/print_distribution.cpp.s"
	/Library/Developer/CommandLineTools/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211/print_distribution.cpp" -o CMakeFiles/print_distribution.dir/print_distribution.cpp.s

# Object files for target print_distribution
print_distribution_OBJECTS = \
"CMakeFiles/print_distribution.dir/print_distribution.cpp.o"

# External object files for target print_distribution
print_distribution_EXTERNAL_OBJECTS =

print_distribution: CMakeFiles/print_distribution.dir/print_distribution.cpp.o
print_distribution: CMakeFiles/print_distribution.dir/build.make
print_distribution: CMakeFiles/print_distribution.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir="/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211/cmake-build-debug/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable print_distribution"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/print_distribution.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/print_distribution.dir/build: print_distribution

.PHONY : CMakeFiles/print_distribution.dir/build

CMakeFiles/print_distribution.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/print_distribution.dir/cmake_clean.cmake
.PHONY : CMakeFiles/print_distribution.dir/clean

CMakeFiles/print_distribution.dir/depend:
	cd "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211/cmake-build-debug" && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211" "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211" "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211/cmake-build-debug" "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211/cmake-build-debug" "/Users/gongjingyang/Desktop/ECNU/Courses Documents/Algorithm/program/exp-20201211/cmake-build-debug/CMakeFiles/print_distribution.dir/DependInfo.cmake" --color=$(COLOR)
.PHONY : CMakeFiles/print_distribution.dir/depend

