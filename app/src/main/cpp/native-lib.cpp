#include <jni.h>
#include <string>
#include "edge-impulse-sdk/classifier/ei_run_classifier.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_empresa_aplicacionedgeimpulse_MainActivity_runInference(JNIEnv *env, jobject /* this */, jfloatArray data) {

    // Obtener el tamaño del arreglo desde Kotlin
    jsize length = env->GetArrayLength(data);

    // Validar el tamaño de la ventana. EI_CLASSIFIER_DSP_INPUT_FRAME_SIZE viene de model_variables.h
    if (length != EI_CLASSIFIER_DSP_INPUT_FRAME_SIZE) {
        return env->NewStringUTF("Error: Tamaño de ventana incorrecto");
    }

    // Extraer los punteros de los datos de Kotlin hacia C++
    jfloat *raw_data = env->GetFloatArrayElements(data, nullptr);

    // Preparar la señal para el SDK de Edge Impulse
    signal_t signal;
    int err = numpy::signal_from_buffer(raw_data, length, &signal);
    if (err != 0) {
        env->ReleaseFloatArrayElements(data, raw_data, 0);
        return env->NewStringUTF("Error: Creación de señal fallida");
    }

    // Ejecutar el clasificador
    ei_impulse_result_t result = { 0 };
    EI_IMPULSE_ERROR res = run_classifier(&signal, &result, false);

    // Liberar la memoria en el lado de la JVM
    env->ReleaseFloatArrayElements(data, raw_data, 0);

    if (res != EI_IMPULSE_OK) {
        return env->NewStringUTF("Error: Fallo en inferencia");
    }

    // Encontrar la clase predicha con la mayor probabilidad
    float max_value = 0.0f;
    std::string top_label = "desconocido";

    for (uint16_t i = 0; i < EI_CLASSIFIER_LABEL_COUNT; i++) {
        if (result.classification[i].value > max_value) {
            max_value = result.classification[i].value;
            top_label = result.classification[i].label;
        }
    }

    return env->NewStringUTF(top_label.c_str());
}