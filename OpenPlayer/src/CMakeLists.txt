# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#add_library( # Sets the name of the library.
#        libvorbis
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        )
add_library( # Sets the name of the library.
        vorbis-jni

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        ../jni/libogg/src/framing.c
        ../jni/libogg/src/bitwise.c
        ../jni/libvorbis/lib/block.c
        ../jni/libvorbis/lib/info.c
        ../jni/libvorbis/lib/synthesis.c
        ../jni/libvorbis/lib/registry.c
        ../jni/libvorbis/lib/codebook.c
        ../jni/libvorbis/lib/sharedbook.c
        ../jni/libvorbis/lib/psy.c
        ../jni/libvorbis/lib/window.c
        ../jni/libvorbis/lib/envelope.c
        ../jni/libvorbis/lib/mdct.c
        ../jni/libvorbis/lib/lpc.c
        ../jni/libvorbis/lib/smallft.c
        ../jni/libvorbis/lib/bitrate.c
        ../jni/libvorbis/lib/floor0.c
        ../jni/libvorbis/lib/floor1.c
        ../jni/libvorbis/lib/lsp.c
        ../jni/libvorbis/lib/res0.c
        ../jni/libvorbis/lib/mapping0.c
        ../jni/decodefeed/DecodeFeed.c
        ../jni/libvorbis-jni/org_xiph_vorbis_decoderjni_VorbisDecoder.c
        )

include_directories(../jni/libvorbis/include)
include_directories(../jni/libogg/include)
include_directories(../jni/decodefeed)
include_directories(../jni/libvorbis-jni)


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        vorbis-jni

        ${log-lib} )