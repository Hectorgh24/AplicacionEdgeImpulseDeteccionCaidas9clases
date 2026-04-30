# AplicacionEdgeImpulseDeteccionCaidas

## Descripcion
Aplicacion Android para deteccion de caidas usando un modelo de Edge Impulse ejecutado localmente (on-device) mediante JNI/C++.

La app toma datos del acelerometro, ejecuta inferencia con TensorFlow Lite Micro (incluido en `edge-impulse-sdk`) y, cuando detecta una posible caida con alta confianza, activa un flujo de alerta.

## Funcionalidades
- Monitoreo en tiempo real del acelerometro.
- Clasificacion local con modelo Edge Impulse.
- Soporte para clases de caida y clases no-caida.
- Pantalla de alerta con cuenta regresiva de 5 segundos.
- Cancelacion manual de alerta.
- Protocolo de emergencia automatico al terminar la cuenta regresiva:
  - Envio de SMS.
  - Llamada telefonica.
  - Apertura de WhatsApp con mensaje prellenado.

## Clases detectadas por la app
En el codigo de la app, las clases consideradas como caida son:
- `fall_backward`
- `fall_bending`
- `fall_forward`
- `fall_hand`
- `fall_sideward_left`
- `fall_sideward_right`
- `fall_sitting`
- `fall_syncope`

Ademas, la interfaz contempla etiquetas no-caida como `walk`, `stand`, `sit`, `idle`, `normal` y `running` (si existen en la salida del modelo).

## Arquitectura del proyecto
- `app/src/main/java/com/empresa/aplicacionedgeimpulse/MainActivity.kt`
  - Captura datos del acelerometro.
  - Llena el buffer de features.
  - Llama a `runClassification(...)` por JNI.
  - Decide si dispara alerta por umbral.
- `app/src/main/java/com/empresa/aplicacionedgeimpulse/AlertActivity.kt`
  - Maneja la cuenta regresiva y el protocolo de emergencia.
- `app/src/main/cpp/native-lib.cpp`
  - Puente JNI.
  - Ejecuta `run_classifier(...)` de Edge Impulse.
- `app/src/main/cpp/edge-impulse-sdk/`
  - SDK y runtime de inferencia embebida.
- `app/src/main/cpp/tflite-model/` y `model-parameters/`
  - Modelo exportado y parametros.

## Requisitos
- Android Studio (recomendado version reciente).
- Android SDK configurado.
- NDK instalado (el proyecto usa compilacion nativa C++).
- CMake instalado (en este proyecto se usa 3.22.1).
- JDK 11 o superior para Gradle/AGP actual.
- Dispositivo Android con acelerometro.

## Compilacion
1. Abrir el proyecto en Android Studio.
2. Verificar que el Gradle JDK sea 11+.
3. Sincronizar Gradle.
4. Compilar y ejecutar en dispositivo fisico.

Si aparece un error de C++ al linkear simbolos de TensorFlow Lite Micro, revisar que en `app/src/main/cpp/CMakeLists.txt` se incluyan fuentes `*.cc` de `edge-impulse-sdk/tensorflow` y `edge-impulse-sdk/third_party`.

## Uso
1. Ingresar un numero telefonico de 10 digitos.
2. Presionar "Iniciar Monitoreo".
3. La app mostrara la prediccion y porcentaje de confianza.
4. Si detecta una caida (con confianza >= 0.85), abre la pantalla de alerta.
5. Si no se cancela en 5 segundos, ejecuta el protocolo de emergencia.

## Permisos utilizados
- `android.permission.INTERNET`
- `android.permission.SEND_SMS`
- `android.permission.CALL_PHONE`

Nota: para SMS y llamada, el usuario debe conceder permisos en tiempo de ejecucion.

## Limitaciones actuales
- El numero telefonico se maneja en formato de 10 digitos (logica orientada a Mexico).
- WhatsApp se abre con prefijo `52` fijo en el numero.
- No incluye, por ahora, persistencia de historial de eventos.
- El comportamiento real depende del modelo exportado en `tflite-model`.

## Estructura de carpetas (resumen)
- `app/src/main/java/` codigo Kotlin de UI y logica.
- `app/src/main/cpp/` JNI, modelo y SDK de inferencia.
- `app/src/main/res/` layouts, recursos visuales y strings.

## Creditos
- Edge Impulse por el SDK y flujo de exportacion del modelo.
- TensorFlow Lite Micro incluido dentro del SDK exportado.

## Licencia
Este repositorio usa el codigo y componentes incluidos en el proyecto.
Revisar las licencias dentro de:
- `app/src/main/cpp/edge-impulse-sdk/LICENSE`
- `app/src/main/cpp/edge-impulse-sdk/LICENSE.3rd-party.txt`
