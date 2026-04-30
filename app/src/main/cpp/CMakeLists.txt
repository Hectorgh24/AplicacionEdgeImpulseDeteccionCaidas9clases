cmake_minimum_required(VERSION 3.4.1)

project("aplicacionedgeimpulse")

include_directories(
    .
    edge-impulse-sdk
    model-parameters
    tflite-model
)

file(GLOB_RECURSE ei_cpp_sources
    edge-impulse-sdk/dsp/kissfft/*.cpp
    edge-impulse-sdk/dsp/dct/*.cpp
    edge-impulse-sdk/dsp/memory.cpp
    edge-impulse-sdk/porting/android/*.cpp
    edge-impulse-sdk/porting/android/*.c
    tflite-model/*.cpp
    tflite-model/*.c
    edge-impulse-sdk/classifier/*.cpp
    edge-impulse-sdk/classifier/*.c
    edge-impulse-sdk/tensorflow/*.cpp
    edge-impulse-sdk/tensorflow/*.cc
    edge-impulse-sdk/tensorflow/*.c
    edge-impulse-sdk/third_party/*.cpp
    edge-impulse-sdk/third_party/*.cc
    edge-impulse-sdk/third_party/*.c
)

add_library(
        aplicacionedgeimpulse
        SHARED
        native-lib.cpp
        ${ei_cpp_sources}
)

target_compile_definitions(aplicacionedgeimpulse PRIVATE
    EI_PORTING_ANDROID=1
    EIDSP_USE_CMSIS_DSP=0
    EIDSP_LOAD_CMSIS_DSP_SOURCES=0
)

find_library(
        log-lib
        log
)

target_link_libraries(
        aplicacionedgeimpulse
        ${log-lib}
)
