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
- `fall_backward` (caida hacia atras)
- `fall_bending` (caida al agacharse)
- `fall_forward` (caida hacia adelante)
- `fall_hand` (caida con apoyo de manos)
- `fall_sideward_left` (caida lateral izquierda)
- `fall_sideward_right` (caida lateral derecha)
- `fall_sitting` (caida al sentarse)
- `fall_syncope` (sincope / desmayo)

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
4. Si detecta una caida (con confianza >= 0.90 en 3 ventanas consecutivas), abre la pantalla de alerta.
5. Si no se cancela en 5 segundos, ejecuta el protocolo de emergencia.

## Permisos utilizados
- `android.permission.INTERNET`
- `android.permission.SEND_SMS`
- `android.permission.CALL_PHONE`
- `android.permission.WAKE_LOCK` (Para ejecución continua en segundo plano)

Nota: para SMS y llamada, el usuario debe conceder permisos en tiempo de ejecucion.

## Mejoras recientes

### Interfaz principal
- Se corrigió el posicionamiento vertical del contenido. Con `targetSdk 36` (Android 15+), el sistema fuerza modo edge-to-edge y el contenido se dibujaba detrás del ActionBar. Se agregó `fitsSystemWindows="true"` a los layouts.
- Se agregó el emoji de la bandera de México (🇲🇽) antes del prefijo `+52` para indicar que solo se aceptan números mexicanos. El emoji se establece programáticamente en Kotlin para evitar problemas de codificación Unicode en XML.
- Se agregaron colores de texto explícitos (`?android:attr/textColorPrimary`) para garantizar visibilidad en modo oscuro.

### Pantalla de ajustes
- Se reemplazó `@android:drawable/dialog_holo_light_frame` (siempre blanco) por `MaterialCardView`, que adapta automáticamente su fondo al tema claro u oscuro.
- Se eliminó `fillViewport="true"` que causaba que el contenido se solapara con el ActionBar en modo edge-to-edge.
- Se agregó `supportActionBar?.title = "Ajustes"` para mostrar un título descriptivo en la toolbar.

### Corrección de falsas detecciones consecutivas post-caída
Después de una caída real detectada correctamente, la app disparaba alertas consecutivas incluso estando inmóvil. Se identificaron y corrigieron las siguientes causas:
- **Cooldown insuficiente**: se aumentó de 3 segundos a 30 segundos para dar tiempo de recuperación.
- **Ventanas consecutivas**: se aumentó de 2 a 3 ventanas consecutivas con confianza >= 90% para confirmar una caída.
- **Buffer contaminado**: al volver de la AlertActivity, el buffer del acelerómetro conservaba datos residuales de la caída. Ahora se limpia con `featuresBuffer.fill(0f)`.
- **Estabilización post-alerta**: se descartan las primeras 2 ventanas de inferencia después de cancelar o completar una alerta, para que el sensor se estabilice.
- **Timestamp de cooldown**: ahora se reinicia desde el momento en que el usuario vuelve a MainActivity, no desde cuando se detectó la caída original.

### Ejecución en Segundo Plano (WakeLock) y Temporizador
- Se integró `PowerManager.WakeLock` (PARTIAL_WAKE_LOCK) para asegurar que el procesador siga recibiendo eventos del sensor incluso si el usuario apaga la pantalla, garantizando que no se pierdan datos durante el monitoreo real.
- Se implementó un `CountDownTimer` de 120 segundos que se muestra en la pantalla principal. Una vez que finaliza, el monitoreo se detiene automáticamente y los datos se guardan de forma síncrona, evitando la pérdida de información por interrupciones.

### Optimización de Rendimiento en Logs (MonitoringLogManager)
- Se reescribió el motor de logs usando un **Ring Buffer** basado en arreglos primitivos (`FloatArray`, `LongArray`) para eliminar la saturación del *Garbage Collector* (zero object allocation) y prevenir congelamientos en sesiones prolongadas.
- Las escrituras al archivo JSON ahora se delegan a un hilo secundario (`ExecutorService`), evitando cualquier bloqueo en la interfaz de usuario.
- Se implementó una lógica de compensación de inferencias para asegurar una precisión de milisegundos en la generación del JSON de salida, con gráficos diezmados (*decimated*) para mantener una interfaz rápida y fluida en la pestaña de ajustes.

### Herramientas de Python para Reconstrucción (Python Tools)
- Se creó la carpeta `python_tools/` que incluye dos scripts especializados (`interfaz_grafica.py` y `generar_videos.py`) para este modelo de 9 clases.
- Permite la reconstrucción post-monitoreo de videos animados a 1080p y 30 FPS que muestran la evolución de la predicción (línea de tiempo) y de la física bruta del sensor (acelerómetro).
- Cuenta con validaciones automáticas, detección de múltiples archivos JSON y uso de aceleración por hardware (NVENC) con un fallback a decodificación CPU ultrarápida.

## Limitaciones actuales
- El numero telefonico se maneja en formato de 10 digitos (logica orientada a Mexico).
- WhatsApp se abre con prefijo `52` fijo en el numero.
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


## 🔬 Integración con Orquestador Multimodelo (Actualización)
Esta aplicación fue modificada para operar simultáneamente con otros 3 modelos de Inteligencia Artificial en un solo dispositivo (Poco F7) durante protocolos de investigación científica.

### Mejoras Críticas Implementadas:
1. **Inmunidad en Segundo Plano (DummyForegroundService)**: Se implementó un servicio en primer plano vinculado a una notificación de alta prioridad (IMPORTANCE_HIGH) para engañar al gestor de batería de Android 14. Esto permite que el acelerómetro siga registrando datos a 100Hz aunque la app esté minimizada.
2. **Permisos de Sincronización de Datos**: Se inyectaron los permisos FOREGROUND_SERVICE_DATA_SYNC y POST_NOTIFICATIONS en el AndroidManifest.xml para cumplir con las políticas de seguridad de Android 14.
3. **Arranque Forzado en onResume**: Se modificó MainActivity.kt para garantizar que el servicio de recolección se dispare automáticamente al abrir la aplicación, haciéndola resistente a cierres del sistema.
4. **Sincronización UDP**: La aplicación escucha en el puerto 50000 comandos de un orquestador central (Python) para iniciar y detener la recolección de telemetría (JSON) exactamente al mismo milisegundo que los otros modelos.

### ⏱️ Rendimiento de Generación de Videos (Aceleración AMF)
Durante las pruebas de campo en un equipo HP Victus (AMD Radeon RX 6550M), el renderizado de gráficos de la telemetría tardó lo siguiente:
* **Video de Línea de Tiempo (Predicciones)**: ~20 minutos (151.58 MB)
* **Video de Acelerómetro (Ejes X,Y,Z)**: ~15 minutos (89.97 MB)
* **Tiempo Total por Ciclo (120s)**: ~35 minutos.
> Nota: El renderizado se realiza cuadro por cuadro en Python (Matplotlib) antes de ser comprimido por la GPU.
