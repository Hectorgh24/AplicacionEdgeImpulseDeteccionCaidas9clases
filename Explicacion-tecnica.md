# Arquitectura y Flujo de Detección de Caídas: Edge Impulse (9 Clases)

Este documento detalla la estructura lógica y el flujo de datos del repositorio `AplicacionEdgeImpulseDeteccionCaidas9clases`. A diferencia de los enfoques tradicionales basados en TensorFlow Lite, este proyecto NO maneja archivos `.keras` ni `.tflite`. En su lugar, el flujo de detección de clases delega la computación pesada directamente a un SDK pre-compilado nativo en C/C++ proporcionado por Edge Impulse.

## 1. El Núcleo de Inferencia: Ausencia de `.keras` y puente JNI
Al usar Edge Impulse, la arquitectura de la red neuronal y los parámetros de procesamiento de señales (DSP) se exportan de la nube directamente como código fuente en C++ (C++ SDK) y se compilan usando CMake dentro de Android Studio (`libaplicacionedgeimpulse.so`).
* **Inexistencia de Intérprete TFLite:** No hay un modelo `.keras` que cargar ni interpretar dinámicamente en RAM. La red neuronal está embebida y "hardcodeada" en la lógica del binario C++ de la aplicación.
* **El Puente JNI (Java Native Interface):** Para que Kotlin (el mundo Android) y C++ (el modelo) puedan hablar, el archivo `MainActivity.kt` declara una función externa: `external fun runClassification(features: FloatArray): String`. Esta pasarela envía los datos crudos a C++ y devuelve un texto plano con la clasificación de la IA.

## 2. Flujo de Datos para la Detección de Clases

El ciclo de los datos está severamente optimizado en arreglos unidimensionales (Zero-Allocation) para eludir el Recolector de Basura (Garbage Collector) de Java y garantizar ultra-baja latencia (Bare-Metal).

### A. Adquisición (Productor) - `MainActivity.kt (onSensorChanged)`
1. **Suscripción de Hardware:** El `MainActivity` engancha directamente al acelerómetro con `SENSOR_DELAY_GAME` para garantizar una inyección constante de **50Hz**.
2. **Inyección en Buffer Plano:** Se prescinde de listas complejas. Se utiliza `featuresBuffer`, un simple `FloatArray` estático de tamaño 300 (100 muestras * 3 ejes). Cada iteración de hardware inyecta secuencialmente [X, Y, Z].
3. **Deslizamiento (Sliding Window):** Una vez que se llenan las 300 posiciones, se realiza una copia profunda para enviar a la IA, e inmediatamente se aplasta la mitad del arreglo (`System.arraycopy`) moviéndolo 150 elementos a la izquierda. Esto simula un retroceso temporal de 1 segundo, permitiendo entregar predicciones continuas.

### B. Inferencia (Procesamiento Nativo C++)
1. **Asincronía Atómica:** El motor C++ bloquea la ejecución del procesador hasta terminar la matemática matricial. Por ello, la llamada `runClassification` se envuelve en un `ExecutorService` (Hilo secundario). 
2. **Prevención de Ahogo:** Se usa una bandera `AtomicBoolean` (`inferenceInProgress`). Si la CPU está caliente y la inferencia C++ toma más de 1 segundo, la nueva ventana de datos que escupe el sensor se tira a la basura (Dropped Frame) y se duplica el valor lógico anterior. Esto impide desbordamientos de memoria (`Out of Memory`).
3. **Respuesta Rudimentaria:** C++ no retorna arreglos bonitos, sino un String separado por pipes: `Clase|Confianza` (ej. `fall_backward|0.98`).

### C. Evaluación (Consumidor) - `MainActivity.kt (Lógica de Decisión)`
1. **Parseo Textual:** Kotlin recibe el string bruto de C++, limpia el ruido (caracteres `\u0000`) y extrae el flotante de confianza matemática.
2. **El Juez Supremo:** Se evalúan tres reglas absolutas para activar el protocolo SOS:
   * ¿La clase extraída pertenece al catálogo de las clases peligrosas (`FALL_CLASSES`)?
   * ¿El porcentaje de confianza es mayor o igual al **75%** (`FALL_THRESHOLD = 0.75f`)?
   * ¿El celular NO está ya detonando un SOS en este instante (`!isAlertActive`)?
3. **Disparo de Intent Visual:** Si se superan las tres reglas, se lanza `AlertActivity.kt` de manera abrupta sobre la pantalla, iniciando sirenas e inyectando un estado rojo al gestor de Logs.

## 3. Gestor de Telemetría: `MonitoringLogManager.kt`
Archivo dictatorial de la persistencia de datos (Tesis IoT). Opera con `CopyOnWriteArrayList` y arreglos primitivos fijos de tamaño 7000, los cuales NUNCA se expanden. Esto logra un **Zero-Allocation Log** que previene los *micro-congelamientos* en Android a mitad de prueba. Absorbe métricas RAM, acelerómetro 50Hz crudo, y etiquetas emitidas por C++. Al culminar 120s, un hilo oscuro exporta herméticamente a un `monitoring_log.json` dentro de `Descargas`.

## Resumen del Flujo de Ejecución (Pipeline):
1. **Sensor Táctil (50Hz)** -> `MainActivity.kt`
2. **Buffer Secuencial (300 floats)** -> Sliding Window C-Style
3. **Hilo Aislado Executor** -> `runClassification()` JNI Native Call
4. **C++ NDK Edge Impulse** -> Ejecuta Red Neuronal Bare-Metal 
5. **C++ NDK** -> Retorna String ("fall_backward|0.98") 
6. **Hilo Aislado (Kotlin)** -> Parseo y Validación de Confianza (>0.75) 
7. **Disparo Actividad Pánico** -> `AlertActivity.kt`
8. Simultáneamente: **MonitoringLogManager** guarda a disco JSON esquivando colapsos de memoria.
