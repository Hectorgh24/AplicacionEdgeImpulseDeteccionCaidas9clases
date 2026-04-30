#include <jni.h>
#include <string>
#include <android/log.h>
#include "edge-impulse-sdk/classifier/ei_run_classifier.h"

#define LOG_TAG "EdgeImpulseNative"
#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_empresa_aplicacionedgeimpulse_MainActivity_runClassification(
        JNIEnv* env,
        jobject /* this */,
        jfloatArray features) {

    jfloat* features_data = env->GetFloatArrayElements(features, nullptr);
    jsize features_length = env->GetArrayLength(features);

    if (features_length != EI_CLASSIFIER_DSP_INPUT_FRAME_SIZE) {
        env->ReleaseFloatArrayElements(features, features_data, JNI_ABORT);
        return env->NewStringUTF("ERROR: Tamaño de buffer incorrecto");
    }

    signal_t features_signal;
    int err = numpy::signal_from_buffer(features_data, features_length, &features_signal);
    if (err != 0) {
        env->ReleaseFloatArrayElements(features, features_data, JNI_ABORT);
        return env->NewStringUTF("ERROR: Fallo al crear la señal");
    }

    ei_impulse_result_t result = { 0 };
    EI_IMPULSE_ERROR res = run_classifier(&features_signal, &result, false);

    env->ReleaseFloatArrayElements(features, features_data, JNI_ABORT);

    if (res != 0) {
        return env->NewStringUTF("ERROR: Fallo en el clasificador");
    }

    // Encuentra la clase con mayor probabilidad
    float max_value = 0.0f;
    int max_index = -1;
    for (size_t ix = 0; ix < EI_CLASSIFIER_LABEL_COUNT; ix++) {
        if (result.classification[ix].value > max_value) {
            max_value = result.classification[ix].value;
            max_index = ix;
        }
    }

    std::string result_str = "";
    if (max_index >= 0) {
        result_str = std::string(result.classification[max_index].label) + "|" + std::to_string(max_value);
    } else {
        result_str = "ERROR|0.0";
    }

    return env->NewStringUTF(result_str.c_str());
}
