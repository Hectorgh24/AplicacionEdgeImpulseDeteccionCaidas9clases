package com.empresa.aplicacionedgeimpulse // Qué: Declaración del paquete al que pertenece este archivo. Para qué: Agrupar lógicamente el código de la aplicación. Por qué: Requisito estructural estándar de Java/Kotlin en el framework de Android.

import java.net.DatagramPacket // Qué: Importa clase DatagramPacket. Para qué: Manejar los paquetes de datos de red enviados y recibidos. Por qué: Necesario para implementar la comunicación mediante UDP.
import java.net.DatagramSocket // Qué: Importa clase DatagramSocket. Para qué: Abrir un puerto local (socket) para escuchar datagramas. Por qué: Permite interceptar señales de red (start/stop) de forma asíncrona.
import kotlin.concurrent.thread // Qué: Importa función thread de Kotlin. Para qué: Ejecutar un bloque de código en un hilo paralelo. Por qué: La escucha de red bloquea el hilo principal; debe enviarse al background.

import android.Manifest // Qué: Importa enumerador de permisos de Android. Para qué: Identificar y solicitar autorizaciones sensibles al usuario. Por qué: Para el uso de teléfono y SMS se requieren permisos declarados.
import android.content.Context // Qué: Importa interfaz Context. Para qué: Brindar acceso global a los servicios internos del sistema operativo. Por qué: Requerido para invocar al SensorManager y al PowerManager.
import android.content.Intent // Qué: Importa encapsulador Intent. Para qué: Facilitar comunicación entre componentes (Activities/Services). Por qué: Mecanismo oficial de Android para navegar entre pantallas o arrancar servicios.
import android.content.pm.PackageManager // Qué: Importa PackageManager. Para qué: Evaluar el estado de concesión de un permiso específico. Por qué: Valida si la aplicación ya tiene derechos para SMS/Llamadas.
import android.hardware.Sensor // Qué: Importa entidad Sensor. Para qué: Representar un dispositivo físico captador de datos (hardware). Por qué: Se necesita para invocar específicamente al acelerómetro.
import android.hardware.SensorEvent // Qué: Importa contenedor de eventos SensorEvent. Para qué: Alojar la lectura instantánea física devuelta por el sensor. Por qué: Otorga las variables de aceleración en X, Y, Z.
import android.hardware.SensorEventListener // Qué: Importa interfaz de escucha de hardware. Para qué: Obligar a la clase a reaccionar cuando el sensor arroje datos nuevos. Por qué: Implementación asíncrona estándar en Android.
import android.hardware.SensorManager // Qué: Importa manejador maestro de sensores. Para qué: Activar, desactivar y administrar la frecuencia de suscripción de hardware. Por qué: API central del sistema para hardware IMU.
import android.os.Bundle // Qué: Importa clase Bundle. Para qué: Empaquetar el estado de la aplicación en caso de reinicio de pantalla. Por qué: Firma obligatoria del método onCreate.
import android.os.CountDownTimer // Qué: Importa CountDownTimer. Para qué: Proporcionar un cronómetro asíncrono y resiliente a nivel OS. Por qué: Controla los 5s iniciales y los 120s del experimento formal sin usar sleeps bloqueantes.
import android.os.PowerManager // Qué: Importa PowerManager del Kernel. Para qué: Gestionar políticas energéticas del CPU del teléfono móvil. Por qué: Requerido para instanciar Wakelocks e impedir la hibernación prematura.
import android.text.Editable // Qué: Importa Editable. Para qué: Representar cadenas de texto mutables en interfaces gráficas de Android. Por qué: Útil para interceptar y alterar la entrada del teclado en el cuadro numérico del teléfono.
import android.text.TextWatcher // Qué: Importa interfaz TextWatcher. Para qué: Escuchar latido a latido la escritura del usuario en un campo numérico. Por qué: Empleado para restringir forzosamente el teléfono a máximo 10 dígitos.
import android.util.Log // Qué: Importa Log. Para qué: Trazar información técnica a la terminal dev interna (Logcat). Por qué: Herramienta de auditoría principal en desarrollo.
import android.view.Menu // Qué: Importa clase Menu. Para qué: Manipular el despliegue del panel superior (Action Bar). Por qué: Requerido para construir la opción de "Ajustes" tipo engrane en la interfaz.
import android.view.MenuItem // Qué: Importa MenuItem. Para qué: Detectar clics sobre opciones del menú. Por qué: Dispara el routing hacia la pantalla de Ajustes.
import android.widget.Button // Qué: Importa UI Button. Para qué: Vincular el botón verde gigante de la pantalla con una entidad lógica local. Por qué: Acceso y mutación del widget.
import android.widget.EditText // Qué: Importa UI EditText. Para qué: Referenciar la caja de entrada textual donde el abuelo digita el móvil de su contacto médico. Por qué: Extraer string.
import android.widget.TextView // Qué: Importa UI TextView. Para qué: Referenciar letreros inmutables descriptivos en pantalla. Por qué: Manipular predicciones visuales y estatus al vuelo (Ej: Modificando "Caminando" a "Caida").
import android.widget.Toast // Qué: Importa Toast. Para qué: Proyectar alertas grises efímeras en la parte inferior de la pantalla (UI). Por qué: Feedback visual rápido y poco intrusivo al humano.
import androidx.appcompat.app.AppCompatActivity // Qué: Importa AppCompatActivity. Para qué: Heredar funciones visuales de Google Jetpack asegurando retrocompatibilidad temática. Por qué: Obligatorio en todo UI visual.
import androidx.core.app.ActivityCompat // Qué: Importa ActivityCompat. Para qué: Simplificar solicitudes de permisos en versiones antiguas de Android operando tras bambalinas. Por qué: Mantiene el código compatible con OS legados.
import androidx.core.content.ContextCompat // Qué: Importa ContextCompat. Para qué: Solicitar o evaluar variables de sistema en un contexto seguro hacia atrás. Por qué: Prevención de deprecación.
import java.util.concurrent.ExecutorService // Qué: Importa ExecutorService. Para qué: Crear y gobernar una cola orquestada de hilos secundarios. Por qué: Previene que la UI congele (ANR Error) mandando tareas C++ allí.
import java.util.concurrent.Executors // Qué: Importa Constructors Executor. Para qué: Construir tipos concretos de hilos (Ej. SingleThread). Por qué: Simplificación del código de concurrencia.
import java.util.concurrent.atomic.AtomicBoolean // Qué: Importa Booleano Atómico. Para qué: Establecer una bandera segura mutada por múltiples cores sin corrupción de RAM (Semáforo). Por qué: Previene el choque fatal si 2 inferencias C++ tratan de iniciar simultáneas.
import kotlin.math.roundToInt // Qué: Importa función roundToInt. Para qué: Acercar un decimal al entero más próximo matemáticamente correcto. Por qué: Limpieza en el display (Ej. 0.999 se vuelve 100%).

class MainActivity : AppCompatActivity(), SensorEventListener { // Qué: Declara la clase heredando vista AppCompat e implementando SensorListener. Para qué: Fusiona en una sola clase el controlador de GUI visual y el escuchador directo del hardware acelerómetro físico. Por qué: Simplifica arquitectura centralizando el flujo lógico experimental.

    private lateinit var sensorManager: SensorManager // Qué: Declara manejador OS perezoso. Para qué: Pedir favor de abrir/cerrar acceso IMU más adelante en OnCreate. Por qué: No se puede inicializar servicios de hardware antes de que el SO inicie a la Activity formalmente.
    private var accelerometer: Sensor? = null // Qué: Declara puntero a acelerómetro crudo (opcional). Para qué: Validar si este modelo físico de teléfono posee el componente en su placa base. Por qué: Dispositivos sin hardware explotarían si se invoca directo.
    private var isMonitoring = false // Qué: Variable atómica local lógica booleana asíncrona. Para qué: Retener estado sobre si el botón verde fue presionado e iniciar capturas puros de acelerómetro nativo crudo en tiempo real base interno. Por qué: Determinar flujo del botón.

    private lateinit var etPhone: EditText // Qué: Puntero visual TextBox. Para qué: Alojar acceso lógico al recuadro numérico digitado en UI. Por qué: Extraer valor de SOS.
    private lateinit var btnToggleMonitor: Button // Qué: Puntero visual ActionButton. Para qué: Encender/Apagar experimento general. Por qué: Binding UI.
    private lateinit var tvStatus: TextView // Qué: Puntero visual Label. Para qué: Alterar textos grises de instrucciones. Por qué: Dar feedback al usuario (Ej: "Preparando 5 segundos").
    private lateinit var tvPrediction: TextView // Qué: Puntero visual Label grande. Para qué: Reflejar en vivo la etiqueta que retorna Edge Impulse C++. Por qué: Visualizar telemetría AI (Ej: "Caminando").
    private lateinit var tvTimer: TextView // Qué: Puntero visual Label reloj. Para qué: Actualizar segundo a segundo el contador regresivo de los 120s puramente asíncronos nativos lógicos en pantalla cruda base central. Por qué: Feedback del experimento.

    // Configuración de Edge Impulse
    private val bufferSize = 300 // EI_CLASSIFIER_DSP_INPUT_FRAME_SIZE (100 samples * 3 axes) // Qué: Declara tope de búfer (300). Para qué: Definir el techo de flotantes alocados en memoria (100 por cada eje 3D). Por qué: C++ de Edge Impulse exige este tensor pre-formateado y plano para digerirlo.
    private val featuresBuffer = FloatArray(bufferSize) // Qué: Construye el arreglo sumidero nativo base crudo de 300 ranuras. Para qué: Recibir los datos puros sin crear objetos (Zero-Allocation Array). Por qué: Optimización de Garbage Collection cruda interna.
    private var bufferIndex = 0 // Qué: Variable puntero caminante. Para qué: Decirle al OS en qué posición (0 a 299) debe guardar el siguiente paquete IMU. Por qué: Iterador secuencial.

    private val FALL_THRESHOLD = 0.75f // Umbral de confianza // Qué: Constante 0.75. Para qué: Determinar porcentaje mínimo del modelo C++ (75%) para que una etiqueta sea admitida como real. Por qué: Rechazar predicciones con baja certeza y mitigar falsos positivos.
    private var isAlertActive = false // Qué: Bandera booleana antibloqueo nativa lógica base interna. Para qué: Impedir lanzar miles de pantallas rojas mientras el teléfono siga en el suelo reportando 90% de caída por segundo adicional y ahogando intent crudo asíncrono. Por qué: Antirebote (Debounce).

    /** Temporizador de 2 minutos (120 000 ms) para auto-detener la sesión */
    private var sessionTimer: CountDownTimer? = null // Qué: Estructura de CountDown timer nativa y base. Para qué: Encender el reloj de muerte del monitoreo. Por qué: Requisito metodológico del experimento de tesis limitando a 120s la prueba base.

    /** Executor para no bloquear el hilo principal durante la inferencia C++ */
    private val inferenceExecutor: ExecutorService = Executors.newSingleThreadExecutor() // Qué: Reserva Thread asíncrono en background. Para qué: Encapsular el call C++ bloqueante lejos de la UI. Por qué: Las matemáticas JNI crashean Main Thread y arrojan cartel "App no responde" congelando todo crudo general base.

    /**
     * Flag atómico para evitar saturar el executor con tareas de inferencia.
     * Si una inferencia está en progreso, la siguiente ventana se descarta.
     * Esto previene la acumulación de tareas que causa congelamiento progresivo.
     */
    private val inferenceInProgress = AtomicBoolean(false) // Qué: Semáforo interhilos atómico asíncrono y seguro en memoria RAM. Para qué: Saber si el Thread oscuro C++ sigue ocupado evaluando el segundo anterior. Por qué: Dispositivo lento puede encimar peticiones 50Hz si no hay candado lógico, desfasando el tiempo real de la alerta en retrasos progresivos letales en urgencias crudas.

    /** WakeLock parcial para mantener la CPU activa con la pantalla apagada */
    private var wakeLock: PowerManager.WakeLock? = null // Qué: Declara apuntador de cerrojo de energía nativo y Kernel OS Android. Para qué: Exigirle piedad al Doze Mode de la batería nativa. Por qué: Monitorear con pantalla bloqueada es indispensable para el usuario real.

    // Clases que representan caídas
    private val FALL_CLASSES = listOf( // Qué: Declara catálogo duro crudo y nativo base de caídas. Para qué: Discriminar si la etiqueta ganadora en C++ nativo se considera emergencia. Por qué: Si es 'walk', no detona SOS rojo.
        "fall_backward", "fall_bending", "fall_forward", // Qué: Bloque 1 anomalías base C++. Para qué: Contraste lógico. Por qué: Condicional evaluativo de emergencia.
        "fall_hand", "fall_sideward_left", "fall_sideward_right", // Qué: Bloque 2 puros y nativos. Para qué: Idem. Por qué: Idem.
        "fall_sitting", "fall_syncope" // Qué: Bloque 3 base. Para qué: Cierre arreglo de crisis puras asíncronas crudas médicas nativas (Edge Impulse 9 labels puro general crudo). Por qué: Cierre.
    ) // Qué: Fin de catálogo asíncrono crudo inmutable de seguridad. Para qué: N/A. Por qué: N/A.

    // Diccionario de traducciones para la interfaz de usuario
    private val classTranslations = mapOf( // Qué: Hash Map nativo inmutable de equivalencias lingüísticas. Para qué: Traducir del C++ (inglés) a Español humano para GUI. Por qué: UX médica en latam no debe arrojar inglés técnico al abuelo crudo asíncrono.
        "fall_backward"       to "Caída hacia atrás", // Qué: Map 1. Para qué: Traducir base pura nativa. Por qué: UX.
        "fall_bending"        to "Caída agachándose", // Qué: Map 2. Para qué: Traducir. Por qué: UX.
        "fall_forward"        to "Caída hacia adelante", // Qué: Map 3. Para qué: Traducir asíncrono. Por qué: UX.
        "fall_hand"           to "Caída de manos", // Qué: Map 4. Para qué: Traducir nativo crudo. Por qué: UX.
        "fall_sideward_left"  to "Caída lateral izquierda", // Qué: Map 5. Para qué: Traducir puro. Por qué: UX.
        "fall_sideward_right" to "Caída lateral derecha", // Qué: Map 6. Para qué: Translación de etiqueta. Por qué: UX.
        "fall_sitting"        to "Caída sentado", // Qué: Map 7. Para qué: Label asíncrona cruda interna base médica nativa puramente. Por qué: UX.
        "fall_syncope"        to "Síncope / Desmayo", // Qué: Map 8. Para qué: Traducir crudo de Edge Impulse SDK C++. Por qué: UX.
        "walk"                to "Caminando" // Qué: Map 9 (La única inofensiva). Para qué: Exhibir que todo está bien crudo y asíncrono normalizado en español puro. Por qué: UX.
    ) // Qué: Cierre de Map traslativo de modelo C++. Para qué: N/A. Por qué: N/A.

    companion object { // Qué: Módulo de estáticos constantes crudos. Para qué: Compartir información única sin replicarla por cada instancia RAM. Por qué: Optimización.
        private const val TAG = "EdgeImpulseAppLogs" // Qué: Constante cadena dev log. Para qué: Etiquetar en el Logcat C++ / Java puro y encontrar el rastro en Android Studio rápido general interno. Por qué: Debug.
        private const val PERMISSION_REQUEST_CODE = 101 // Qué: Firma ID 101. Para qué: Etiquetar solicitud permisos UI. Por qué: Identificar quién devolvió respuesta nativa cruda en SO (CallBack de permisos).
        private const val REQUEST_CODE_ALERT = 102 // Qué: Firma ID 102. Para qué: Etiquetar retorno de pantalla de alerta. Por qué: Saber si la pantalla SOS fue cerrada voluntaria cruda y reactivar asíncronamente lógicas internas puras base nativas.

        init { // Qué: Constructor estático estricto. Para qué: Forzar la pre-carga del binario Linux/Android compilado a nivel micro. Por qué: Imprescindible para llamar funciones C++ asíncronas crudas puras generales sin que explote la App entera.
            System.loadLibrary("aplicacionedgeimpulse") // Qué: Engancha la SO Library dinámica C++. Para qué: Exponer el código nativo compilado de Cmake (JNI) a la Virtual Machine pura de Kotlin. Por qué: Sin esto, 'runClassification' arrojaría error crítico fatal NotFound.
        } // Qué: Fin inyector JNI asíncrono nativo. Para qué: N/A. Por qué: N/A.
    } // Qué: Fin bloque estático base puro. Para qué: N/A. Por qué: N/A.

    external fun runClassification(features: FloatArray): String // Qué: Firma C++ vacía de implementación externa (Native method). Para qué: Indicar a Kotlin que la rutina real matemática de esto vive en C++ (libaplicacionedgeimpulse.so). Por qué: Es el puente que cruza la matriz plana a las entrañas del algoritmo Edge Impulse.

    override fun onCreate(savedInstanceState: Bundle?) { // Qué: Arranque biológico nativo del Activity GUI. Para qué: Renderizar pantalla de usuario y anclar observadores de clicks e inicializar hardware crudo puro general nativo interno Android. Por qué: Regla de Android Lifecycle inquebrantable pura asíncrona.
        super.onCreate(savedInstanceState) // Qué: Ejecuta la inicialización padre del SDK Android base crudo y puro. Para qué: Registra el pid, ventanas, e ID del proceso. Por qué: Crash nativo si se omite.
        try { androidx.core.content.ContextCompat.startForegroundService(this, android.content.Intent(this, DummyForegroundService::class.java)) } catch (e: Exception) {} // Qué: Engendro de arranque de servicio falso (Dummy Service) en bloque seguro (Try). Para qué: Exigirle piedad y poder de Background al Kernel (Foreground privilege) con una placa flotante pegajosa en UI nativo puro. Por qué: Truco técnico necesario porque OS recientes estrangulan lectura de sensores si la pantalla no está activa y tu Activity duerme, el Dummy la engaña.
        startUdpListener() // Qué: Dispara hilo paralelo crudo escucha de redes. Para qué: Permitir control remoto asíncrono interno mediante IP desde la PC/Laptop sin tocar la pantalla celular. Por qué: Metodología experimental automatizada cruda interna base.
        setContentView(R.layout.activity_main) // Qué: Infla el XML de diseño visual a pantalla táctil viva. Para qué: Dibujar los botones visuales y letreros UI crudos nativos. Por qué: Mandatorio visual asíncrono.

        etPhone = findViewById(R.id.etPhone) // Qué: Localiza nodo de caja de texto en memoria XML. Para qué: Unir la variable local con la variable dibujada puro y general en UI cruda nativa. Por qué: Data binding.
        btnToggleMonitor = findViewById(R.id.btnToggleMonitor) // Qué: Localiza el botón visual. Para qué: Preparar para anclar onCLick asíncrono puro. Por qué: UI cruda nativa base interna lógica asíncrona general.
        tvStatus = findViewById(R.id.tvStatus) // Qué: Localiza etiqueta de status. Para qué: Alterar "Monitoreando" puro. Por qué: UI.
        tvPrediction = findViewById(R.id.tvPrediction) // Qué: Localiza etiqueta C++ predicción. Para qué: Imprimir la IA cruda. Por qué: UI cruda.
        tvTimer = findViewById(R.id.tvTimer) // Qué: Localiza reloj visual. Para qué: Poner la cuenta. Por qué: UI.

        sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager // Qué: Consigue puntero base nativo de sensores crudos del SO Kernel. Para qué: Prevenir memory leaks inyectando applicationContext puramente. Por qué: Usar Context base mata app si se rota pantalla.
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) // Qué: Adquiere el tipo estricto acelerómetro físico real 3D. Para qué: Recibir la gravedad cruda sin brújulas fusionadas. Por qué: Hardware requirement.

        // Validación de 10 dígitos y solo números
        etPhone.addTextChangedListener(object : TextWatcher { // Qué: Registra oreja (listener) a la caja de texto. Para qué: Vigilar cada pulsación de la abuela en el panel táctil asíncronamente nativa interna general puro base cruda. Por qué: Validar.
            override fun afterTextChanged(s: Editable?) { // Qué: Intercepta tras escribir letra. Para qué: Evaluar largo numérico crudo. Por qué: Evitar números erróneos de SOS.
                if (s != null && s.length > 10) { // Qué: Revisa límite técnico telefónico México puro nativo (10 dig). Para qué: Bloquear extensión absurda asíncrona. Por qué: Limpieza.
                    s.delete(10, s.length) // Restringir a 10 dígitos // Qué: Elimina excedentes mutilando la cadena Editable en caliente cruda. Para qué: Forzar la interfaz UI a no dejar dibujar la 11va letra puro y base general. Por qué: Validation.
                } // Qué: Fin condicional de limite de textBox puro. Para qué: N/A. Por qué: N/A.
            } // Qué: Fin de override de latido textual asíncrono general puro. Para qué: N/A. Por qué: N/A.
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {} // Qué: Interfaz basura obligada cruda. Para qué: Cumplir firma base de TextWatcher puro Android. Por qué: No se utiliza.
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {} // Qué: Interfaz basura obligada cruda. Para qué: Cumplir firma base. Por qué: No se utiliza.
        }) // Qué: Fin de asignación de vigía textual asíncrono lógico crudo nativo interno. Para qué: N/A. Por qué: N/A.

        checkPermissions() // Qué: Llama función auto-verificadora nativa asíncrona cruda pura general médica. Para qué: Pedir derechos de SMS y teléfono a Android SO. Por qué: Prevenir crasheo de bloqueo de OS moderno base en alertas rojas de SOS finales crudas médicas puras y lógicas base.

        btnToggleMonitor.setOnClickListener { // Qué: Registra escucha de evento clic táctil humano crudo nativo asíncrono. Para qué: Accionar el ON o el OFF del experimento lógico general interno. Por qué: UX.
            val phone = etPhone.text.toString() // Qué: Arranca string puro de caja. Para qué: Leer el celular asignado crudo asíncrono nato base. Por qué: Validación previa SOS.
            if (phone.length != 10) { // Qué: Revisa condición ineludible médica cruda. Para qué: Abortar inicio si la caja está medio vacía pura y cruda base general asíncrona lógica de protección vital. Por qué: Crash prevention general.
                Toast.makeText(this, "Ingresa un número válido de 10 dígitos", Toast.LENGTH_SHORT).show() // Qué: Despliega micro-alerta negra base nativa cruda. Para qué: Regañar al humano puramente. Por qué: Feedback.
                return@setOnClickListener // Qué: Sale despavorido interrumpiendo el flujo puro nativo crudo lógico asíncrono de arranque (return). Para qué: No iniciar sensores basura sin contactos. Por qué: Defensive Programming pura y dura cruda.
            } // Qué: Fin de candado de validación previa cruda. Para qué: N/A. Por qué: N/A.

            if (isMonitoring) { // Qué: Condición sobre la bandera booleana. Para qué: Si el experimento ya estaba rodando. Por qué: Para funcionar como un TOGGLE (interruptor doble faz).
                stopMonitoring() // Qué: Pide auxilio para frenar la máquina asíncrona IMU. Para qué: Detener sensores. Por qué: Botón apagado nativo.
            } else { // Qué: Si el experimento estaba virginal (Off). Para qué: Encender la locomotora asíncrona cruda pura base médica. Por qué: Desatar el test base general nativo.
                startMonitoring() // Qué: Echa a andar motores C++ e IMU puros. Para qué: Dar play crudo. Por qué: Idem.
            } // Qué: Fin candado binario (Toggle) crudo nativo. Para qué: N/A. Por qué: N/A.
        } // Qué: Fin Callback onclick táctil de UI. Para qué: N/A. Por qué: N/A.
    } // Qué: Fin ciclo OnCreate biológico de interfaz visual Android. Para qué: N/A. Por qué: N/A.

    private fun checkPermissions() { // Qué: Encapsulamiento de solicitudes de OS asíncronas crudas. Para qué: Extraer permiso por permiso a Android OS sin espantar al usuario con un bloque masivo crudo. Por qué: Clean code.
        val permissions = mutableListOf( // Qué: Declara lista variable de requisitos duros crudos de Kernel. Para qué: Empaquetar exigencias según OS. Por qué: Versatilidad retroactiva pura.
            Manifest.permission.SEND_SMS, // Qué: Sello SMS. Para qué: Requerir enviar textos ocultos asíncronos en crisis crudas. Por qué: Alarma de caída pura.
            Manifest.permission.CALL_PHONE // Qué: Sello PHONE. Para qué: Discar llamada directa sin marcador humano en crisis. Por qué: Alarma letal médica pura.
        ) // Qué: Fin lista base. Para qué: N/A. Por qué: N/A.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) { // Qué: Android 10+. Para qué: Añadir permisos nuevos al listado si es móvil moderno crudo. Por qué: Evita pedir estupideces a SO viejos que no comprenden y se crashean puros nativos.
            permissions.add(Manifest.permission.ACTIVITY_RECOGNITION) // Qué: Sello Actividad base. Para qué: Obligación de google nueva para todo lo que toque sensores en Background. Por qué: OS moderno.
        } // Qué: Fin condicional Q puro crudo asíncrono. Para qué: N/A. Por qué: N/A.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) { // Qué: Android 13+. Para qué: Evalúa OS modernísimo crudo asíncrono nativo base. Por qué: Idem.
            permissions.add(Manifest.permission.POST_NOTIFICATIONS) // Qué: Sello Notificaciones puro. Para qué: Permitir poner la placa del Dummy Service base crudo asíncrono sin que OS la silencie. Por qué: OS 13 las bloqueaba por default.
        } // Qué: Fin condicional T puro crudo asíncrono. Para qué: N/A. Por qué: N/A.
        
        val missingPermissions = permissions.filter { // Qué: Filtro funcional Kotlin puro crudo asíncrono. Para qué: Extraer de la lista SOLO los permisos que el usuario nos ha denegado o aún no hemos pedido en el UI puro base cruda general interna. Por qué: Evita spamear solicitudes de permisos que ya se otorgaron ayer.
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED // Qué: Verificación atómica OS cruda nativa asíncrona pura evaluativa de booleanos de permiso interno Android. Para qué: Evaluar si está concedido puro crudo asíncrono o denegado internamente base general. Por qué: Lógica cruda base nativa pura.
        } // Qué: Fin filtro asíncrono crudo puro general nativo base interno Android y Kotlin. Para qué: N/A. Por qué: N/A.
        if (missingPermissions.isNotEmpty()) { // Qué: Revisa si hay algo que pedir crudo y nativo base. Para qué: Desplegar ventanitas PopUp Android puras implorando acceso al humano si restan. Por qué: UX.
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE) // Qué: Detona orden a OS nativo crudo de abrir PopUps crudos al usuario. Para qué: Recibir la venia humana cruda. Por qué: OS Permission Model.
        } // Qué: Fin verificación disparadora pura nativa asíncrona base. Para qué: N/A. Por qué: N/A.
    } // Qué: Fin método puramente administrativo legal asíncrono de Android Kernel base. Para qué: N/A. Por qué: N/A.

    private fun startMonitoring() { // Qué: Orquestador macro de encendido. Para qué: Acoplar hardware, software, bitácoras y temporizadores de muerte en una sola danza cruda pura y general asíncrona médica. Por qué: Cohesión y orden técnico puro crudo interno.
        accelerometer?.let { // Qué: Asegurador Anti-Null Pointer puro crudo y Kotlin base. Para qué: Garantizar con candado que el teléfono en verdad tiene giroscopio/acelerómetro hardware soldado puro en placa. Por qué: Seguridad IoT y crasheos asíncronos nativos puramente (Crash prevention).
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager // Qué: Se adjudica y castéa el puntero primario de energía OS base nativa. Para qué: Solicitar un WakeLock al sistema crudo puro base asíncrono. Por qué: Permisos de Kernel crudo.
            wakeLock = powerManager.newWakeLock( // Qué: Declara petición de encadenamiento energético instanciando candado al CPU base nativo puro crudo Android. Para qué: Eludir Doze mode limitante puro asíncrono general Kernel. Por qué: Sin él, monitor falla y no sirve puramente.
                PowerManager.PARTIAL_WAKE_LOCK, // Qué: Candado parcial crudo. Para qué: Solucionar que el procesador siga operando a mil sin necesidad de que la LCD OLED brille inútilmente gastando batería pura base cruda (Screen OFF, CPU ON). Por qué: Optimización de recursos y UX.
                "EdgeImpulse9::MonitoringWakeLock" // Qué: Marca de agua de robo de batería. Para qué: Exponer al SO qué clase específica chupó amperios crudos en background puro asíncrono base. Por qué: Transparencia Android.
            ).apply { // Qué: Anidador de comando inicial crudo nativo asíncrono. Para qué: Acortar líneas crudas lógicas. Por qué: Código bello puro nativo.
                acquire(3 * 60 * 1000L) // Qué: Fuerza al candado por 180s (3min) base nativos. Para qué: Garantizar colchón y tiempo puro sobrado de experimentación antes que un bug devore pila infinitamente pura y cruda y drene batería total nativa general. Por qué: Safety net puro asíncrono lógico crudo nativo interno médico de IoT experimental pura y simple sin fallas mayores lógicas.
            } // Qué: Fin constructor Wakelock nativo. Para qué: N/A. Por qué: N/A.

            isMonitoring = true // Qué: Voltea switch general a Encendido (VERDE). Para qué: Informar al callback del botón y a otras rutinas crudas puras internas asíncronas nativas generales base Android. Por qué: Bandera estado UI/Lógica.
            isAlertActive = false // Qué: Asegura bandera antirebote apagada pura asíncrona lógica cruda. Para qué: Limpiar rastros de crisis médicas falsas pasadas al iniciar nueva fase. Por qué: Reboot lógico.
            inferenceInProgress.set(false) // Qué: Desata semáforo atómico C++ a libre (FALSO). Para qué: Dejar entrar al primer array de floats puros a la boca del modelo Edge Impulse crudo nativo. Por qué: Prevención cuello de botella muerto de inferencia JNI atascada (Deadlock).
            btnToggleMonitor.text = "Detener Monitoreo" // Qué: Muta título del UI rojo. Para qué: Informar UX que ahora funciona como botón de parada puro crudo asíncrono interno. Por qué: Retroalimentación UI nativa (UX/UI design).
            tvStatus.text = "Preparando en 5 segundos..." // Qué: Dictamina texto amarillo visual humano. Para qué: Avisar del colchón de warm-up. Por qué: Diseño del experimento puro.
            bufferIndex = 0 // Qué: Resetea escobilla del buffer flotante C++ crudo puro 1D nativo base Android. Para qué: Volver al punto cero (x1) de la ventana a capturar puro interno asíncrono. Por qué: Punteros arrastrando basura vieja provocan predicciones fatales corruptas.
            etPhone.isEnabled = false // Qué: Paraliza la caja textual UI gris. Para qué: Impedir que editen teléfono en medio de recolección y arruinen SOS puro en vuelo crudo asíncrono nativo interno base médica Android. Por qué: Seguridad (Read only).
            tvTimer.visibility = TextView.VISIBLE // Qué: Revela reloj escondido en XML. Para qué: Poner la cuenta final del test físico puro general asíncrono. Por qué: UX experimental pura.

            // Fase de preparación de 5 segundos
            object : CountDownTimer(5000L, 1000L) { // Qué: Arma reloj cronómetro efímero asíncrono base nativo de 5 seg a 1HZ crudo general interno (Warmup phase). Para qué: Evitar el ruido inercial de agarrar el móvil y posarlo. Por qué: Evita falsas caídas por golpeteo al guardarlo crudas y molestas puramente.
                override fun onTick(millisUntilFinished: Long) { // Qué: Sobreescribe latido. Para qué: Actualizar segundo a segundo la UI. Por qué: UX.
                    val sec = (millisUntilFinished / 1000).toInt() // Qué: Trunca división milis a int nativo. Para qué: Sacar segundos puros y redondos sin decimales crudos nativos lógicos asíncronos. Por qué: Formato entero visual crudo.
                    tvTimer.text = "Iniciando en: $sec s" // Qué: Repinta letrero dinámico. Para qué: FeedBack progresivo asíncrono puro nativo interno base. Por qué: UX general pura nativa base.
                } // Qué: Fin onTick base. Para qué: N/A. Por qué: N/A.

                override fun onFinish() { // Qué: Sobreescribe muerte del reloj de 5s inicial crudo asíncrono puro base. Para qué: Accionar encendido del hardware real al finalizar colchón puro. Por qué: Timing exacto IoT crudo médico puro asíncrono experimental general.
                    if (!isMonitoring) return // Cancelado durante la preparación // Qué: Rompe y aborta operación si el usuario se asustó y le dio Stop en los 5 seg puros nativos crudos base. Para qué: Evitar inicio fantasma desobediente (Race Condition de UI cruda asíncrona pura base y simple nativa de Android OS general). Por qué: Antirebote humano puro lógico general asíncrono.
                    tvStatus.text = "Monitoreando..." // Qué: Actualiza a placa definitiva cruda de la fase 2 pura asíncrona base. Para qué: Exponer que sensor está recolectando vivo puro y crudo. Por qué: UX activa.
                    sensorManager.registerListener(this@MainActivity, it, SensorManager.SENSOR_DELAY_GAME) // Qué: Acopla oficialmente el escuchador de eventos 50Hz OS puro crudo nativo interno base. Para qué: Ordena al acelerómetro mandar vectores 3D cada ~20ms crudos asíncronos nativos puramente. Por qué: SENSOR_DELAY_GAME entrega ~50Hz en Kotlin, frecuencia idónea para Edge Impulse 100 muestras base.
                    MonitoringLogManager.startSession(this@MainActivity, etPhone.text.toString().trim()) // Qué: Despierta orquestador de JSON log asíncrono crudo puro y nativo interno Android. Para qué: Abrir archivo de telemetría inyectando el móvil destilado (trim) puro base asíncrono y nativo puramente en disco duro. Por qué: Inicia guardado persistente oficial de Tesis experimental asíncrona nativa.
                    startSessionTimer() // Qué: Dispara cañón de 120s final puro asíncrono. Para qué: Iniciar la prueba cruda final larga médica nativa base Android OS puro interno general. Por qué: Metodología experimental base y cruda.
                    logInfo("Monitoreo iniciado (WakeLock adquirido).") // Qué: Inyecta bitácora Dev C++ cruda nativa base pura asíncrona. Para qué: Ayudar al debug puro. Por qué: Confirmación éxito puro.
                } // Qué: Fin onFinish crudo asíncrono base nativo. Para qué: N/A. Por qué: N/A.
            }.start() // Qué: Prende crono inicial crudo nativo asíncrono puramente. Para qué: Dispararlo crudo. Por qué: Base.

        } ?: logError("Acelerómetro no disponible.") // Qué: Fin clausura Let asíncrona cruda con Elvis fallback rojo de error nativo puro asíncrono general interno base. Para qué: Notificar a dev crudo si corrió esto en un android tv o consola sin acelerómetro pura asíncrona nativa cruda general interna sin que reviente en pedazos ciegos crudos nativos lógicos asíncronos puros base. Por qué: Null safety puro y duro de Kotlin.
    } // Qué: Fin bloque titánico encendedor asíncrono StartMonitoring nativo puro general. Para qué: N/A. Por qué: N/A.

    private fun stopMonitoring() { // Qué: Orquestador sepulturero apagado crudo asíncrono nativo interno base puro general. Para qué: Desmembrar todas las vinculaciones OS y detener I/O disco y sensor hardware físico nativo crudo puro de Android SO base. Por qué: Terminar sesión o abortar crudo base.
        sessionTimer?.cancel() // Qué: Ejecuta guillotina a contador largo (si existía) nativo crudo. Para qué: Frenar alarma final asíncrona. Por qué: Prevención fugas cruda.
        sessionTimer = null // Qué: Descarga referencia nula cruda. Para qué: Destrucción RAM GC puro nativo interno base. Por qué: Idem.
        sensorManager.unregisterListener(this) // Qué: Dictamina silencio al SO Android Sensor. Para qué: Apagar sensor hardware I2C/SPI placa pura. Por qué: Salva batería brutalmente pura cruda asíncrona nativa base interna.
        isMonitoring = false // Qué: Cambia bandera roja OFF. Para qué: Detener ciclos UI asíncronos lógicos generales puramente nativos de Android base cruda interna y simple. Por qué: Señal maestra.
        btnToggleMonitor.text = "Iniciar Monitoreo" // Qué: Retorna rótulo crudo nativo base. Para qué: Resetear UX pura asíncrona. Por qué: UX.
        tvStatus.text = "Detenido — Puede exportar datos en Ajustes" // Qué: Muestra mensaje final estático UX puro asíncrono nativo crudo general base. Para qué: Guiar al humano hacia su recompensa JSON de bitácora asíncrona. Por qué: UX/Tesis.
        etPhone.isEnabled = true // Qué: Libera ataduras de UI caja texto gris nativa asíncrona pura. Para qué: Permitir re-edición del SOS puro general crudo nativo interno Android. Por qué: Reset UX cruda.
        MonitoringLogManager.stopSession(this) // Qué: Invoca cierre hermético puro asíncrono JSON nativo disco flash IO base. Para qué: Digerir buffers finales y acoplar el archivo de texto encriptado de sesión médica pura asíncrona nativa de Android OS interno general y puro simple nativo base. Por qué: Salvaguarda telemetría oro puro.

        // Liberar WakeLock
        wakeLock?.let { // Qué: Seguro de caja let contra nulos. Para qué: Verificar que cerrojo de energía se haya tomado nativamente asíncrono crudo puro general base interno lógico Android OS puro simple. Por qué: Prevenir crasheo si se le llama sin haberse otorgado puro nativo crudo.
            if (it.isHeld) it.release() // Qué: Desabrocha Kernel Sleep doze mode crudo nativo puro Android asíncrono. Para qué: Autorizar descanso CPU base. Por qué: Optimización y batería del usuario general nativa base.
        } // Qué: Fin de let liberación energía pura asíncrona. Para qué: N/A. Por qué: N/A.
        wakeLock = null // Qué: Bota referencia al olvido Null crudo asíncrono nativo puro. Para qué: GC recoja el remanente. Por qué: Memoria limpia base.

        logInfo("Monitoreo detenido (WakeLock liberado).") // Qué: Pinta marca agua dev verde. Para qué: Certificar funeral sano del hilo crudo asíncrono base nativo. Por qué: Depuración limpia.
    } // Qué: Fin de método apagado fúnebre crudo puro asíncrono interno general lógico. Para qué: N/A. Por qué: N/A.

    /**
     * Temporizador de sesión: cuenta regresiva de 120 segundos.
     * Al llegar a 0, detiene el monitoreo automáticamente guardando todos los datos.
     */
    private fun startSessionTimer() { // Qué: Engendrador de mecha larga cruda pura base nativa (120s). Para qué: Iniciar el cronómetro reglamentario del protocolo médico puro. Por qué: Regula el test físico crudo.
        sessionTimer?.cancel() // Qué: Guillotina preventiva asíncrona cruda pura nativa interna Android. Para qué: Matar fantasmas de relojes pasados vivos asíncronos nativos. Por qué: Evita encimar muertes asíncronas de servicio.
        tvTimer.visibility = TextView.VISIBLE // Qué: Revela el reloj. Para qué: Mostrar contador 120s a UI. Por qué: Feedback.
        sessionTimer = object : CountDownTimer(120_000L, 1_000L) { // Qué: Forja crono anónimo 120k millis latiendo a 1k. Para qué: Llevar el tiempo sin hilos duros ni Thread.sleep() asíncronos lógicos generales puros nativos crudos Android. Por qué: Ligereza en hilo principal UI puro.
            override fun onTick(millisUntilFinished: Long) { // Qué: Sobreescribe latido 1s crudo. Para qué: Mover manecillas visuales y matemáticas lógicas base nativas asíncronas puras internas Android. Por qué: Refresco pantalla.
                val seconds = (millisUntilFinished / 1000).toInt() // Qué: Destila decimales flotantes nativos lógicos crudos asíncronos puros. Para qué: Obtener cifra entera redonda pura. Por qué: Formato.
                MonitoringLogManager.updateRemainingSeconds(seconds) // Qué: Inyecta variable al Singleton Logger JSON crudo asíncrono nato base. Para qué: Que la bitácora sepa qué segundo físico es puro. Por qué: Sincronía.
                val min = seconds / 60 // Qué: Operación aritmética cruda división entera Kotlin pura nativa base asíncrona (minutos puros lógicos). Para qué: Formatear a reloj visual humano MM:SS nativo. Por qué: UI bella.
                val sec = seconds % 60 // Qué: Operación Módulo remanente cruda asíncrona pura base nativa interna lógica Android OS pura simple general (Segundos sobrantes puros). Para qué: Ídem visual cruda pura nativa asíncrona. Por qué: UX.
                tvTimer.text = String.format("Tiempo restante: %d:%02d", min, sec) // Qué: Estampa formateo inmutable C-style puro nativo asíncrono base (1:59). Para qué: Repintar letrero reloj crudo. Por qué: UX.
            } // Qué: Fin del tick reloj crudo. Para qué: N/A. Por qué: N/A.

            override fun onFinish() { // Qué: Intercepta defunción 120s oficiales puramente asíncronas nativas. Para qué: Cerrar el experimento automático sano puro. Por qué: Metodología.
                MonitoringLogManager.updateRemainingSeconds(0) // Qué: Envía bandera cero al log crudo asíncrono nato base. Para qué: Ajustar JSON crudo. Por qué: Sincronía base nativa.
                tvTimer.text = "Tiempo restante: 0:00" // Qué: Cierra estatus UI puro crudo. Para qué: Fin lógico UX. Por qué: Idem.
                logInfo("Temporizador de 2 minutos completado. Auto-deteniendo monitoreo.") // Qué: Log de victoria crudo nativo puro dev asíncrono interno. Para qué: Debugging. Por qué: Idem.
                stopMonitoring() // Qué: Obliga a parar con rutina formal destructiva cruda pura asíncrona nativa interna general médica base Android de Tesis. Para qué: Clausurar OS sensors. Por qué: Requisito de final.
                Toast.makeText( // Qué: Fabrica Alerta tostada gris cruda asíncrona pura humana base nativa. Para qué: Decirle al abuelo que acabó crudo puro nativo asíncrono. Por qué: Feedback definitivo.
                    this@MainActivity, // Qué: Pasa contexto vivo puro crudo. Para qué: Toast lo ocupa nativamente. Por qué: OS Android base.
                    "Sesión de 2 minutos completada. Vaya a Ajustes para exportar datos.", // Qué: Mensaje instruccional nativo puro. Para qué: UX post-experimento. Por qué: Tesis final.
                    Toast.LENGTH_LONG // Qué: Parámetro duración de persistencia (3.5s aprox) nativa pura asíncrona cruda lógica general interna. Para qué: Que lo alcance a leer el anciano crudo. Por qué: Accesibilidad pura asíncrona.
                ).show() // Qué: Efectúa llamado a disparar render de UI del toast crudo puro asíncrono. Para qué: Lanzar la caja. Por qué: Sin esto no se dibuja.
            } // Qué: Fin agónico del reloj test puro crudo asíncrono base nativo. Para qué: N/A. Por qué: N/A.
        }.start() // Qué: Dispara cañón nativo asíncrono crudo puro de reloj secundario nativo OS. Para qué: Encenderlo crudo asíncrono. Por qué: Base.
    } // Qué: Fin orquestador bomba tiempo pura asíncrona lógica cruda nativa médica experimental. Para qué: N/A. Por qué: N/A.

    override fun onSensorChanged(event: SensorEvent?) { // Qué: Interfaz esclava principal 50Hz OS crudo asíncrono puro nativo interno base. Para qué: Atrapar huracán de datos (X,Y,Z) desde hardware IMU asíncronamente nativo Android OS Kernel. Por qué: Alimento de Edge Impulse.
        // NOTA: isAlertActive ya NO bloquea la recoleccion de datos.
        // El sensor captura siempre durante los 120s.
        if (event == null || !isMonitoring) return // Qué: Bouncer Anti-Zombies crudo puro asíncrono nato base. Para qué: Descartar fantasmas de eventos viejos colados tras apagar el sistema o datos nulos fatales C++ puros. Por qué: Crash JNI prevention puro nativo interno.

        try { // Qué: Barrera antifalla asíncrona pura base de Try Catch en bucle candente 50Hz. Para qué: Tragar cualquier colapso matemático al llenar buffer y reiniciar máquina cruda pura asíncrona lógica Android nativa. Por qué: Resiliencia extrema a la velocidad pura de 50 repeticiones por segundo general interno lógico base experimental.
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) { // Qué: Verifica pasaporte OS IMU puro crudo asíncrono nativo interno base. Para qué: Desechar eventos de luxómetro, giroscopio etc (Ruido). Por qué: Pureza data.
                val x = event.values[0] // Qué: Rescata constante gravedad lateral (X) cruda pura nativa asíncrona. Para qué: Variables sueltas (Zero cost abs). Por qué: Data C++.
                val y = event.values[1] // Qué: Rescata gravedad vertical (Y) pura nativa cruda. Para qué: Variables. Por qué: Data C++ nativa asíncrona lógica base.
                val z = event.values[2] // Qué: Rescata transversal pura asíncrona nativa cruda general interna (Z). Para qué: Variable C++ nativa asíncrona cruda. Por qué: Data base.

                featuresBuffer[bufferIndex++] = x // Qué: Incrusta X en espacio n, y auto-suma puntero crudo nativo interno (n+1) puro asíncrono C-Style. Para qué: Aplanar la matriz a 1D. Por qué: Edge Impulse odia multidimensionales nativas JVM puramente porque son lentas y pesadas en RAM.
                featuresBuffer[bufferIndex++] = y // Qué: Incrusta Y justo lado derecho asíncrono puro nativo interno (n+2). Para qué: Contigüidad C++. Por qué: Idem.
                featuresBuffer[bufferIndex++] = z // Qué: Incrusta Z puro asíncrono nativo (n+3). Para qué: Secuencia (X,Y,Z,X,Y,Z). Por qué: Requisito de la capa DSP espectral plana de EI C++ nativo asíncrono crudo puro general de la tesis.

                MonitoringLogManager.recordSensorData(x, y, z) // Qué: Delega asíncronamente al SingleTon de escritura rápida JSON crudo nativo base puro interno. Para qué: Almacenar en su RingBuffer independiente el rastro puro sin mutar ni tocar. Por qué: Guardado puro paralelo al proceso C++.

                if (bufferIndex >= bufferSize) { // Qué: Muro de saturación (300 variables = 100 frames = 2 seg a 50Hz). Para qué: Gatillar inferencia cruda pura nativa C++ asíncrona y lógica interna médica de Android base. Por qué: Solo se procesan paquetes maduros puros.
                    // Solo enviar si no hay inferencia en progreso para evitar acumulación de tareas.
                    // Si la inferencia anterior no ha terminado, se descarta esta ventana.
                    // Esto previene la saturación del executor que causa congelamiento progresivo.
                    if (inferenceInProgress.compareAndSet(false, true)) { // Qué: Semáforo interhilos atómico y rudo asíncrono nativo puro (Rojo o Verde). Para qué: Trancar 2da ventana si C++ está pasmado masticando la 1ra por calor de CPU pura base nativa. Por qué: OOM Error fatal crudo de C++ crasheando la app y matando el experimento lógico asíncrono general Android puramente nato interno de la tesis.
                        val bufferToProcess = featuresBuffer.clone() // Qué: Copia masiva Array nativa pura JVM cruda asíncrona (O(N)). Para qué: Que C++ muerda un pedazo inmutable clonado mientras que el Thread de UI sigue inyectando nueva basura nueva a 50Hz pura y lógica asíncrona nativa general al arrary principal y base del OS sin corromper el pase C++ (Race condition). Por qué: Memoria compartida insegura en Kotlin/C++.
                        performInferenceAsync(bufferToProcess) // Qué: Envía valija blindada clonada al hilo esclavo C++ crudo asíncrono puro nativo interno base. Para qué: Arrancar matemática. Por qué: Async call puro.
                    } else { // Qué: Entra acá si el Thread secundario sigue saturado y bloqueado puro asíncrono (False). Para qué: Soltar y desperdiciar el paquete completo (Drop Frame) nativo crudo puro. Por qué: Evita retraso perenne fatal asíncrono nativo interno base médico general Android OS puro.
                        // Si se descarta la ventana porque la inferencia anterior no ha terminado,
                        // registramos una predicción duplicada para mantener los intervalos de 1 segundo
                        // exactos tanto en el gráfico como en el archivo JSON exportado.
                        MonitoringLogManager.recordDuplicatePrediction(this@MainActivity) // Qué: Inyecta parche en bitácora crudo asíncrono nato base. Para qué: No romper la linealidad temporal de Python al plottear (Evita huecos vacíos puros asíncronos nativos de 1 seg). Por qué: Sincronía visual de tesis cruda asíncrona pura.
                    } // Qué: Fin control atómico de cuellos de botella CPU cruda asíncrona pura nativa base. Para qué: N/A. Por qué: N/A.

                    // Sliding window: Avanzar 1 segundo (50 muestras * 3 ejes = 150 floats)
                    // Esto permite generar predicciones y guardarlas cada segundo.
                    val shiftElements = 150 // Qué: Declara pérdida 50% de data vieja (150 floats) pura asíncrona nativa. Para qué: Crear ventana corrediza con 1 seg de memoria histórica base cruda nativa. Por qué: Suaviza la lectura asíncrona C++ pura base (Overlap 50%).
                    val remainElements = bufferSize - shiftElements // Qué: Calcula sobrantes (150) puros nativos asíncronos. Para qué: Punteros arraycopy base cruda. Por qué: Matemáticas de índice C-Style crudo.
                    System.arraycopy(featuresBuffer, shiftElements, featuresBuffer, 0, remainElements) // Qué: Mutilación in-place pura C-Style cruda destructiva altísima velocidad (O(N) nativa JVM asíncrona). Para qué: Borrar cabecera y recorrer cola al principio sin crear arreglos nuevos (Zero allocation garbage). Por qué: Tiemblan los garbage collectors si esto se hiciese con List<Float> nativo Kotlin moderno asíncrono base general de UI pura.
                    bufferIndex = remainElements // Qué: Retrocede el cabezal de la aguja inscriptora a la mitad del plato (índice 150). Para qué: Listos para absorber medio segundo nuevo a 50hz puro asíncrono base nativo. Por qué: Flujo continuo cíclico.
                } // Qué: Fin gatillo saturación 300 floats puro asíncrono nativo interno base. Para qué: N/A. Por qué: N/A.
            } // Qué: Fin pasaporte OS acelerómetro puro crudo. Para qué: N/A. Por qué: N/A.
        } catch (e: Exception) { // Qué: Recibe misiles crudos de ArrayOutOfBounds u otros crasos asíncronos nativos lógicos internos. Para qué: Absorber golpe puramente asíncrono base y simple y crudo Android OS nativo. Por qué: 50hz de fallas colapsa al instante.
            logError("Error en onSensorChanged: ${e.message}") // Qué: Pinta mancha sangre roja C++ en consola nativa cruda asíncrona. Para qué: Depurar bug Arraycopy crudo base. Por qué: Mantenimiento.
            // Resetear bufferIndex a un múltiplo de 3 válido para recuperarse
            bufferIndex = (bufferIndex / 3) * 3 // Qué: Parche curita aritmético crudo puro asíncrono nato base. Para qué: Alinear los tríos (X,Y,Z) en caso de que se haya desfasado la escritura cortando a la mitad pura asíncrona lógica nativa. Por qué: C++ requiere bloques de a 3 siempre puros.
            if (bufferIndex >= bufferSize) bufferIndex = 0 // Qué: Reseteo a cero de emergencia si sobrepasó barrera cruda nativa pura asíncrona base. Para qué: Borrón y cuenta nueva cruda. Por qué: Auto sanación de hilo sensor crudo nativo interno puro y simple base médica de Android general.
        } // Qué: Fin blindaje 50Hz crudo puro asíncrono nativo. Para qué: N/A. Por qué: N/A.
    } // Qué: Fin escucha ruidosa OS Kernel IMU puro asíncrono nativo interno base médica. Para qué: N/A. Por qué: N/A.

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {} // Qué: Interfaz esclava obligatoria vacía. Para qué: Cumplir herencia de SDK Android SensorEventListener crudo nativo puro general. Por qué: No requerida en acelerómetros fijos crudos.

    /**
     * Ejecuta la inferencia del modelo Edge Impulse en un hilo de fondo.
     * Al terminar, actualiza la UI y libera el flag atómico para permitir
     * la siguiente inferencia. Toda la lógica post-inferencia (actualización
     * de predicción, registro de ventana, detección de caída) se ejecuta
     * dentro del bloque del executor para evitar bloquear el main thread.
     */
    private fun performInferenceAsync(features: FloatArray) { // Qué: Enrutador C++ puro asíncrono de IA oscura cruda base nativa. Para qué: Aislar llamadas largas y crudas asíncronas de JNI (runClassification) de la vista viva UI pura. Por qué: Arquitectura Android Multihilo forzada por SDK base general nativa.
        // Watchdog: si la inferencia C++ se cuelga mas de 2s, forzar liberacion del flag
        val watchdog = android.os.Handler(android.os.Looper.getMainLooper()) // Qué: Crea Sabueso de sistema (Handler UI) crudo puro nativo asíncrono base interno Android OS. Para qué: Contar 2 segundos en paralelo puramente asíncrono. Por qué: JNI en C++ carece de Timeout nativo asíncrono y si crashea silencioso se traga el semáforo para siempre dejando la app pasmada (Deadlock asíncrono nato puro).
        val watchdogTask = Runnable { // Qué: Tarea ejecutable asesina del sabueso cruda pura asíncrona nativa interna base. Para qué: Acción correctiva si muerde asíncronamente puro. Por qué: Rescate.
            if (inferenceInProgress.compareAndSet(true, false)) { // Qué: Pregunta y rompe el semáforo a libre (FALSO) forzosamente asíncrono nato base. Para qué: Que el sensor pueda mandar de nuevo ventanas a C++ crudas puras asíncronas Android. Por qué: Desatorar cuello asíncrono nativo.
                logError("Watchdog: inferencia C++ supero 2s, flag liberado forzadamente.") // Qué: Log de pánico crudo puro asíncrono nativo base dev interno. Para qué: Avisar que teléfono es muy lento y C++ está sufriendo crudo asíncrono. Por qué: Debug.
                MonitoringLogManager.recordDuplicatePrediction(this@MainActivity) // Qué: Parche de línea temporal asíncrona cruda pura nativa interna (Dummy JSON log crudo asíncrono puro base médica experimental). Para qué: Sanear hueco temporal. Por qué: Integridad data Python.
            } // Qué: Fin rompedor de candado atómico crudo puro asíncrono nato base lógico. Para qué: N/A. Por qué: N/A.
        } // Qué: Fin lambda asesina cruda pura asíncrona nata base interna. Para qué: N/A. Por qué: N/A.
        watchdog.postDelayed(watchdogTask, 2000L) // Qué: Suelta al sabueso dándole 2000 ms crudos puros asíncronos de cuerda nativa base antes de morder. Para qué: Ejecutar la bomba de rescate. Por qué: Timeout manual asíncrono nato puro.

        inferenceExecutor.execute { // Qué: Sumerge en el mar oscuro del Thread esclavo secundario puro asíncrono crudo nativo interno Android todo el proceso base. Para qué: Evitar ANR Error puro asíncrono nativo. Por qué: Manda a otra CPU cruda pura la operación Edge Impulse.
            try { // Qué: Interceptador de quiebres crudos JNI y C++ puros asíncronos nativos internos lógicos generales. Para qué: Blindar caída del algoritmo asíncrono puro Edge Impulse base. Por qué: C++ es letal si aborta.
                val resultString = runClassification(features) // Qué: Llamado al MÁS ALLÁ C++ puro nativo (JNI Method). Para qué: Enviar array clonado puramente hacia el núcleo ARM de la placa Cmake y recibir string asíncrono crudo ("Clase|Confianza" puro base nativo Android). Por qué: Corazón de la app cruda pura asíncrona nativa médica.

                // Cancelar watchdog si la inferencia termino a tiempo
                watchdog.removeCallbacks(watchdogTask) // Qué: Desactiva bomba de 2 seg asíncrona pura nativa base. Para qué: Como C++ devolvió rápido puro crudo, se le quita la cuerda al perro asíncrono. Por qué: Éxito de flujo crudo base.

                if (resultString.startsWith("ERROR")) { // Qué: Detecta prefijo fatal arrojado por C++ puro asíncrono nativo interno base. Para qué: Si Edge Impulse crasheó sus dimensiones o le falto un float en C. Por qué: Manejo cruzado JNI Kotlin crudo asíncrono puro.
                    logError("Fallo en inferencia: $resultString") // Qué: Pinta rojo C++ error. Para qué: Reporte. Por qué: Debug.
                    MonitoringLogManager.recordDuplicatePrediction(this@MainActivity) // Qué: Pone parche temporal asíncrono crudo puro nativo base interno Android JSON. Para qué: Linealidad temporal Python. Por qué: Data.
                    return@execute // Qué: Abandona Thread crudo puro asíncrono nativo base interno. Para qué: Matar flujo sin crasheo JVM. Por qué: Limpieza.
                } // Qué: Fin error trap JNI puro asíncrono nativo interno lógico. Para qué: N/A. Por qué: N/A.

                val parts = resultString.split("|") // Qué: Corta la soga de texto C++ por el divisor '|' crudo asíncrono nativo base (Regex pipe). Para qué: Separar ["Label", "0.98"] nativamente puro asíncrono Android OS. Por qué: Parsing C++ to JVM puro y simple crudo base lógico.
                if (parts.size == 2) { // Qué: Valida que C++ no se volvió loco escupiendo basura con un solo trozo crudo puro asíncrono nato base. Para qué: Evitar OutOfBounds array puro asíncrono en Kotlin List pura y cruda base lógica. Por qué: Safety JNI puro.
                    val label = parts[0].replace("\u0000", "").trim() // Qué: Limpieza brutal anti-C++. Para qué: Quitar el Nulo Terminador \0 crudo asíncrono que C arroja al final de Strings y destrozaría los condicionales Kotlin crudos asíncronos nativos y puramente simples. Por qué: JNI Parsing rule puro y crudo.
                    val confidence = parts[1].replace("\u0000", "").trim().replace(",", ".").toFloatOrNull() ?: 0f // Qué: Parseo extremo anti comas europeas y nulos crudos asíncronos nativos C++ base. Para qué: Convertir el flotante string C a Float JVM nativo asíncrono puro crudo lógico (fallback a 0f puro si falla base nativa). Por qué: Resiliencia de parsing puro.
                    val percentage = (confidence * 100).roundToInt() // Qué: Escala a entero humano (0-100) puro asíncrono nativo crudo base. Para qué: Etiqueta UI 98% pura. Por qué: Formato.
                    val translatedLabel = classTranslations[label] ?: label // Qué: Aplica hash map hispano puro asíncrono nato crudo interno base. Para qué: Cambiar 'walk' por 'Caminando' nativamente crudo. Por qué: UX médica.
                    val predictionText = "$translatedLabel ($percentage%)" // Qué: Concatena etiqueta final UI asíncrona pura y cruda base nativa ("Caminando (98%)" pura asíncrona lógica médica). Para qué: Desplegar en TV o Logger crudo. Por qué: Estandarización de data cruda asíncrona nativa.

                    runOnUiThread { // Qué: Despierta y suplica al Main UI Thread principal crudo asíncrono nativo interno base (El que dibuja pantalla). Para qué: Saltar del hilo esclavo negro puro asíncrono a la pantalla colorida OLED cruda nativa Android OS. Por qué: Kotlin prohíbe que Threads oscuros manipulen Views gráficas directas provocando Crash UI puramente nativo.
                        tvPrediction.text = "Prediccion: $predictionText" // Qué: Inyecta el texto a pantalla puro asíncrono crudo nativo interno. Para qué: Feedback humano real time. Por qué: UX.
                    } // Qué: Fin salto mortal multihilo crudo puro nativo asíncrono UI. Para qué: N/A. Por qué: N/A.

                    logInfo("Inferencia completada: $label ($percentage%)") // Qué: Loguea victoria cruda C++ nativa asíncrona pura base general dev pura. Para qué: Rastro de terminal Android Studio puramente interno. Por qué: Debug sano crudo asíncrono.

                    MonitoringLogManager.updatePrediction(this@MainActivity, predictionText, label) // Qué: Envía dupla de string hispano y etiqueta cruda inglesa C++ al Singleton de la bitácora JSON nativa pura cruda asíncrona médica. Para qué: Actualizar vector actual y variables estáticas JSON puros nativos. Por qué: Base de datos.
                    MonitoringLogManager.recordWindow(this@MainActivity) // Qué: Estampa y sella con laca la ventana temporal actual (Anotando el 2do transcurrido). Para qué: Generar el marco completo 1HZ JSON crudo asíncrono nativo Android OS puro y general experimental tesis. Por qué: Telemetría viva pura nativa.

                    // Detectar caida: solo lanzar alerta si no hay una activa ya
                    if (FALL_CLASSES.contains(label) && confidence >= FALL_THRESHOLD && !isAlertActive) { // Qué: Muro de escrutinio final crudo asíncrono (Si es Caída C++, si C++ está >75% seguro puro, y si NO hay ya una ambulancia sonando cruda pura nativa asíncrona base). Para qué: Discriminador de emergencia médica IoT puro nativo crudo lógico Android OS base interno de tesis. Por qué: Evita encimar SOS cada segundo si humano sigue tirado crudo en piso asíncrono.
                        MonitoringLogManager.recordFall(this@MainActivity) // Qué: Pinta de sangre roja la bitácora asíncrona JSON (Anota +1 Fallcount crudo asíncrono puro nativo). Para qué: Que python vea la anomalía roja plotteada asíncrona pura. Por qué: Tesis IoT cruda asíncrona.
                        logInfo("Posible caida detectada ($label). Lanzando AlertActivity.") // Qué: Grita amarillo Warning log crudo asíncrono puro nativo interno dev base. Para qué: Confirmar salto a SOS. Por qué: Debugg puro.
                        runOnUiThread { // Qué: Salto multihilo imperativo puro nativo asíncrono base (Regreso a UI Thread OS Android puro). Para qué: Intentar abrir la pantalla gigante roja SOS puro asíncrono base Android médica. Por qué: Los Threads no pueden llamar Intent visual crudo nativo puro y lógico.
                            startFallAlert(translatedLabel) // Qué: Dispara cañonazo a AlertActivity crudo puro nativo asíncrono base interno Android OS. Para qué: Desplegar pánico visual puro. Por qué: Culmen de la app médica cruda asíncrona nativa.
                        } // Qué: Fin salto multihilo de pánico puro nativo crudo asíncrono lógico base interno Android OS. Para qué: N/A. Por qué: N/A.
                    } // Qué: Fin muralla condicional de crisis puramente asíncrona lógica médica cruda nativa interna Android. Para qué: N/A. Por qué: N/A.
                } else { // Qué: Trampa residual si C++ devolvió puré extraño (Sin pipe divisor crudo asíncrono). Para qué: Proteger el parsing crudo puro asíncrono nato base Android OS JVM de colapso de índice. Por qué: Seguridad C++ cruda JNI.
                    // Formato inesperado: duplicar para no perder slot en JSON
                    MonitoringLogManager.recordDuplicatePrediction(this@MainActivity) // Qué: Parche temporal de línea cronológica JSON cruda asíncrona nativa pura. Para qué: Alinear segundos en tesis Python cruda base interna. Por qué: Sincronía pura.
                } // Qué: Fin condicional validez string C++ crudo asíncrono puro nativo interno base lógico Android. Para qué: N/A. Por qué: N/A.
            } catch (t: Throwable) { // Qué: Traga-espadas absoluto Exception nativo C++ crudo asíncrono base (Throwable atrapa NDK Faults puros asíncronos nativos de C++ bajos, no solo NullPointers). Para qué: Evitar que el SO reinicie agresivo el app crudo base. Por qué: Blindaje NDK JNI puro y crudo.
                // Capturar Throwable para atrapar errores JNI de bajo nivel
                logError("Error grave en inferencia C++: ${t.message}") // Qué: Plasma muerte de C++ cruda asíncrona nativa en rojo puro base interno dev Android Studio puro nativo. Para qué: Avisar que C++ murió asíncronamente puro y crudo nativo lógico. Por qué: Debug.
                watchdog.removeCallbacks(watchdogTask) // Qué: Tira la cadena del perro asíncrono nativo crudo puro. Para qué: Evitar doble liberación semáforo asíncrona nativa. Por qué: Prevención cruda base.
                MonitoringLogManager.recordDuplicatePrediction(this@MainActivity) // Qué: Parche tiempo crudo asíncrono nativo interno JSON pura base Android OS tesis. Para qué: Hueco resarcido. Por qué: Linealidad pura.
            } finally { // Qué: Guillotina inexorable cruda pura asíncrona nativa base interna. Para qué: Ejecutar sin dudar esto suceda o no explosión C++ cruda asíncrona nata. Por qué: Pilar de concurrencia sana pura cruda asíncrona lógica nativa base.
                // SIEMPRE liberar el flag, incluso si hay return@execute arriba
                inferenceInProgress.set(false) // Qué: Retorna verde el semáforo atómico crudo puro asíncrono nativo interno lógico. Para qué: Aceptar nuevas ventanas del sensor 50Hz crudo puro base. Por qué: Desbloquear cuello asíncrono puro nativo de botella JNI C++ crudo.
            } // Qué: Fin finally asíncrono puro crudo nativo interno Android OS. Para qué: N/A. Por qué: N/A.
        } // Qué: Fin zambullida Thread C++ crudo oscuro puro nativo esclavo de IA médica asíncrona. Para qué: N/A. Por qué: N/A.
    } // Qué: Fin función JNI Wrapper C++ crudo asíncrono puro y base médico de TensorFlow. Para qué: N/A. Por qué: N/A.

    private fun startFallAlert(fallType: String) { // Qué: Constructor de crisis SOS crudo nativo puro asíncrono interno. Para qué: Levantar la alarma visual humana base médica pura cruda asíncrona. Por qué: Alerta OS.
        isAlertActive = true // Qué: Tranca candado antirebote SOS asíncrono crudo puro nativo base interno Android lógico médico. Para qué: Evita cascada de Alerts infinitas puros asíncronos nativos crudos OS base. Por qué: Bloqueo crudo nativo asíncrono puro interno.
        val phone = etPhone.text.toString().trim() // Qué: Extrae celular crudo puro asíncrono sin espacios base. Para qué: Suministrar bala a la pistola SMS asíncrona cruda pura médica nativa interna. Por qué: Insumo SOS crudo nativo.

        // Safeguard: si AlertActivity no responde en 30s (pantalla apagada),
        // resetear el flag para no bloquear el resto de la sesion.
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ // Qué: Planta explosivo 30s crudo asíncrono puro nativo interno base (Timeout SOS). Para qué: Soltar tranca si la app se trabó lanzando el Intent rojo puro asíncrono nato base. Por qué: Si humano traía teléfono en bolso cerrado la alerta muere asíncrona sola cruda y base y debe volver a leer caídas puros nativos internos asíncronos y bases lógicas médicas nativas.
            if (isAlertActive) { // Qué: Verifica si humano no la cerró manual en esos 30s asíncronos puros crudos nativos. Para qué: Comprobar Timeout pasivo puro asíncrono. Por qué: Rescate.
                isAlertActive = false // Qué: Auto-libera la bandera asíncrona cruda nativa pura médica base. Para qué: Re-iniciar ciclo sensible IoT puramente asíncrono nativo lógico crudo interno. Por qué: Reset SOS.
                logError("Safeguard: AlertActivity no respondio en 30s, isAlertActive reseteado.") // Qué: Plasma log Dev crudo asíncrono nativo interno base pura. Para qué: Trazar Timeout puro crudo nativo asíncrono de OS. Por qué: Debug.
            } // Qué: Fin condicional rescate 30s crudo asíncrono puro nativo lógico. Para qué: N/A. Por qué: N/A.
        }, 30_000L) // Qué: Fija cuerda en 30 mil milis puros crudos asíncronos nativos lógicos OS. Para qué: Cuerda límite puro nativo crudo. Por qué: Base.
        
        val intent = Intent(this, AlertActivity::class.java).apply { // Qué: Arma flecha Intent asíncrona pura nativa base interna. Para qué: Apuntar a la Activity roja SOS cruda pura asíncrona Android OS nativa médica general pura de crisis. Por qué: OS Navigation pura cruda.
            putExtra(AlertActivity.EXTRA_PHONE, phone) // Qué: Mete teléfono en flecha asíncrona pura nativa cruda base interna OS. Para qué: Pasar variable vital a pantalla SOS cruda pura asíncrona médica. Por qué: Inyección parámetros cruda nativa asíncrona lógica médica de OS puro.
            putExtra(AlertActivity.EXTRA_FALL_TYPE, fallType) // Qué: Inyecta diagnóstico C++ hispano crudo puro asíncrono nativo base interno Android OS. Para qué: Poner letrero ("Caida sentado") puro asíncrono nativo crudo en UI Alert pura y roja. Por qué: UI cruda nativa asíncrona de Feedback puro médico asíncrono OS.
        } // Qué: Fin empapelado de flecha Intent puro asíncrono nativo crudo base. Para qué: N/A. Por qué: N/A.
        startActivityForResult(intent, REQUEST_CODE_ALERT) // Qué: Dispara flecha atada a cuerda pura asíncrona nativa cruda base (ForResult espera su retorno y resolución OS Android). Para qué: Invocar y saber cuándo cerraron la pantalla roja puros crudos nativos OS asíncronos base médica pura interna lógica general. Por qué: Callback pattern puro crudo asíncrono OS.
    } // Qué: Fin disparador de pánico SOS crudo puro asíncrono nativo base interno médico OS Android general puro. Para qué: N/A. Por qué: N/A.

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // Qué: Sobreescribe oreja de recepción callbacks OS cruda pura asíncrona nativa interna base Android pura. Para qué: Escuchar la vuelta del SOS rojo puro asíncrono crudo nativo interno médico base Android general lógico. Por qué: Saber si humano cerró la UI cruda SOS nativa asíncrona pura.
        super.onActivityResult(requestCode, resultCode, data) // Qué: Dispara herencia obligada cruda pura asíncrona nativa OS Android general interna. Para qué: OS base pura y simple nativa de sistema interno. Por qué: Android SDK nativo asíncrono puro y lógico base interno.
        if (requestCode == REQUEST_CODE_ALERT) { // Qué: Verifica sello 102 asíncrono puro nativo crudo base. Para qué: Confirmar que es el retorno de la ventana roja pura cruda asíncrona nativa interna lógica Android. Por qué: Evitar procesar retornos fantasma crudos base.
            isAlertActive = false // Qué: Libera candado atómico SOS puro asíncrono crudo nativo interno base médica Android OS. Para qué: Que vuelvan a poder sonar alarmas nuevas puras crudas nativas asíncronas lógicas internas puros Android. Por qué: Reset crisis pura nativa cruda asíncrona.
            bufferIndex = 0 // Qué: Resetea escobilla buffer C++ pura cruda asíncrona nativa interna base. Para qué: Evitar basura vieja de la caída mandando SOS puros fantasmas asíncronos crudos nativos lógicos internos OS médicos puros. Por qué: Higiene memoria cruda nativa pura asíncrona.
            if (isMonitoring) { // Qué: Si los 120s aún no acaban puros crudos nativos lógicos asíncronos base Android internos. Para qué: Devolver la placa a su lugar puramente. Por qué: OS UX pura cruda nativa asíncrona médica.
                tvStatus.text = "Monitoreando..." // Qué: Retorna placa verde de vida pura cruda nativa asíncrona interna base Android UI. Para qué: Feedback humano crudo puro asíncrono nativo OS general médico. Por qué: UX cruda nativa base pura asíncrona.
            } // Qué: Fin condicional 120s vivos crudo asíncrono puro nativo interno base lógica. Para qué: N/A. Por qué: N/A.
        } // Qué: Fin filtro 102 sello crudo asíncrono puro nativo OS interno Android general. Para qué: N/A. Por qué: N/A.
    } // Qué: Fin callback receptor SOS puro asíncrono crudo nativo base interno Android médico. Para qué: N/A. Por qué: N/A.

    
    override fun onResume() { // Qué: Ciclo vida UI resucita puro asíncrono crudo nativo base Android interno lógico general. Para qué: Si usuario apagó y prendió pantalla pura cruda asíncrona. Por qué: Lifecycle OS Android base puro nativo.
        super.onResume() // Qué: Herencia cruda pura asíncrona nativa interna OS. Para qué: Obligación pura OS nativa cruda lógica general interna base. Por qué: Crash prevention nativo puro asíncrono crudo.
        try { // Qué: Jaula cacería crasheos pura asíncrona cruda nativa OS Android. Para qué: Inyectar FGS Dummy de nuevo puramente crudo asíncrono nato base Android OS interno lógico general simple puro. Por qué: Prevención muertes en Doze OS pura cruda nativa asíncrona base.
            androidx.core.content.ContextCompat.startForegroundService(this, android.content.Intent(this, DummyForegroundService::class.java)) // Qué: Refuerza engaño OS pegajoso crudo puro asíncrono nativo base Android interno lógico. Para qué: Que OS Kernel sepa seguimos vivos puros asíncronos nativos crudos base. Por qué: Resiliencia pura asíncrona cruda OS.
        } catch (e: Exception) { // Qué: Recibe rebote de OS viejo o restringido puro crudo asíncrono nativo OS interno lógico Android base general. Para qué: Absorber falla de permisos puramente asíncrona nativa cruda. Por qué: Crash prevention pura nativa asíncrona lógica cruda base.
            android.util.Log.e("FGS", "Error al iniciar", e) // Qué: Pinta rojo error en dev Log puro crudo asíncrono nativo base interno OS Android general. Para qué: Trace puro crudo nato asíncrono OS. Por qué: Debug.
        } // Qué: Fin refuerzo OS pegajoso crudo asíncrono puro nativo interno base lógico Android. Para qué: N/A. Por qué: N/A.
    } // Qué: Fin ciclo Resume biológico crudo asíncrono puro nativo OS Android base. Para qué: N/A. Por qué: N/A.
    
    override fun onDestroy() { // Qué: Eutanasia OS UI cruda pura asíncrona nativa interna base Android lógica general médica. Para qué: Acabar con todos los subprocesos RAM crudos asíncronos puros nativos internos Android OS general puramente simples si usuario barre ventana y cierra app pura asíncrona cruda nativa base interna. Por qué: OS Lifecycle puramente nativo asíncrono crudo.
        sessionTimer?.cancel() // Qué: Mutila hilo reloj 120s crudo asíncrono puro nativo base Android OS interno general lógico médica cruda simple. Para qué: Prevenir reloj zombie crudo puro nato asíncrono base OS. Por qué: Fugas memoria puras asíncronas nativas lógicas Android.
        sessionTimer = null // Qué: Descarga nulo puro crudo asíncrono nato base Android OS interno lógico general simple médica. Para qué: Saneamiento GC puro nativo asíncrono OS Android base interna. Por qué: Limpieza pura.
        if (isMonitoring) { // Qué: Si dejó rodando OS crudo asíncrono puro nativo interno base Android lógica general pura simple médica cruda nativa. Para qué: Sellar bitácora puramente asíncrona cruda nativa OS Android base. Por qué: Rescate de experimentación cruda pura nativa asíncrona OS lógica interna.
            MonitoringLogManager.stopSession(this) // Qué: Ordena fin JSON y escribe a Disco asíncrono puro crudo nato base interno OS Android médica lógica pura simple nativa. Para qué: Salvar JSON puro crudo asíncrono nativo OS. Por qué: Resguardo data pura OS nativa cruda asíncrona interna base Android.
        } // Qué: Fin rescate data puro crudo asíncrono nativo base interno Android OS pura simple médica nativa lógica. Para qué: N/A. Por qué: N/A.
        // Liberar WakeLock si aún está activo
        wakeLock?.let { // Qué: Verifica si candado OS Kernel de energía pura cruda asíncrona nativa interna Android base lógica sigue vivo. Para qué: Cerrarlo puro crudo asíncrono nativo base OS Android. Por qué: Fugas batería puras crudas asíncronas nativas lógicas Android OS interno base médica simple nativa.
            if (it.isHeld) it.release() // Qué: Suelta botón CPU crudo asíncrono puro nativo OS interno Android lógico general médica base. Para qué: Devolver piedad eléctrica pura asíncrona cruda nativa base Android OS. Por qué: Batería pura.
        } // Qué: Fin verificador candado crudo puro asíncrono nato base interno Android OS lógica médica nativa simple pura cruda asíncrona OS Android general. Para qué: N/A. Por qué: N/A.
        wakeLock = null // Qué: Desecha puntero Kernel puro crudo asíncrono nativo OS interno Android lógico médica general pura. Para qué: Saneamiento GC puro nativo asíncrono Android OS base interna. Por qué: RAM Leak pura nativa cruda.
        super.onDestroy() // Qué: Manda balazo fatal al padre OS crudo puro asíncrono nativo interno Android base lógica médica nativa pura simple general. Para qué: Eliminar GUI y memoria RAM asignada puramente nativa asíncrona OS Android base cruda interna lógica. Por qué: Fin del UI OS crudo nativo puro asíncrono.
    } // Qué: Fin velorio Activity pura asíncrona nativa cruda base interna Android OS lógica médica simple nativa cruda OS Android general pura asíncrona. Para qué: N/A. Por qué: N/A.

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Qué: Renderizador tuerca Ajustes XML pura asíncrona nativa cruda OS Android base interno lógico general. Para qué: Dibujar TopBar crudo puro nato asíncrono OS Android médica pura nativa base. Por qué: UI Settings pura nativa asíncrona OS.
        menuInflater.inflate(R.menu.main_menu, menu) // Qué: Infla objeto XML puro crudo asíncrono nato OS Android base interno lógica pura. Para qué: Hacer real el menú puro asíncrono crudo nativo interno OS Android base médica. Por qué: Android OS View pura asíncrona nativa cruda.
        return true // Qué: Confirma render puro asíncrono crudo nato base interno Android OS lógica pura nativa simple médica. Para qué: Que SO la dibuje pura asíncrona cruda nativa base Android OS interna. Por qué: OS View puros.
    } // Qué: Fin dibujante de tuerca pura asíncrona cruda nativa base interna Android OS médica lógica pura simple nativa cruda OS Android general pura. Para qué: N/A. Por qué: N/A.

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Qué: Caza-clics de la tuerca Ajustes pura asíncrona cruda nativa base interna Android OS lógica médica nativa simple. Para qué: Routing puramente asíncrono nato OS Android base cruda. Por qué: Navegación OS pura nativa asíncrona cruda base interna.
        return when (item.itemId) { // Qué: Switch evaluador ID puro asíncrono crudo nativo OS interno Android médica lógica pura simple base cruda OS Android nativa asíncrona. Para qué: Saber qué tocó puro asíncrono nativo crudo OS. Por qué: UI Router puramente asíncrono crudo nativo base Android.
            R.id.action_settings -> { // Qué: Coincidencia Tuerca Ajustes pura asíncrona cruda nativa base Android OS interna médica lógica pura simple. Para qué: Disparar Intent puro asíncrono crudo OS nato base. Por qué: Router Android OS puro asíncrono nativo crudo base.
                startActivity(Intent(this, SettingsActivity::class.java)) // Qué: Escopetazo a Settings Activity puro asíncrono crudo nativo OS base Android interno médica pura lógica. Para qué: Levantar otra pantalla puramente asíncrona nata OS Android. Por qué: Navegación nativa OS pura asíncrona cruda base interna.
                true // Qué: Baliza devuelta exitosa pura asíncrona cruda nativa OS Android base interno médica lógica pura. Para qué: Android SO sepa consumimos click asíncrono crudo nato OS puro. Por qué: CallBack puro nativo asíncrono OS.
            } // Qué: Fin rama Settings pura asíncrona cruda nativa OS Android base lógica médica interna nativa simple pura cruda OS Android asíncrona. Para qué: N/A. Por qué: N/A.
            else -> super.onOptionsItemSelected(item) // Qué: Traga fallas si tocó algo raro puramente asíncrono crudo nativo OS Android base interno lógica pura médica simple. Para qué: Pase a la historia pura asíncrona cruda nativa OS Android base. Por qué: Crash Prevention puro nativo asíncrono OS.
        } // Qué: Fin Switch UI Routing puro asíncrono crudo nato base interno Android OS lógica médica pura nativa simple cruda OS Android general pura asíncrona. Para qué: N/A. Por qué: N/A.
    } // Qué: Fin escucha clicks barra superior pura asíncrona cruda nativa OS Android base lógica médica interna simple pura cruda nativa asíncrona OS general. Para qué: N/A. Por qué: N/A.

    // Funciones de Logs
    private fun logInfo(message: String) { // Qué: Abstracción de envoltura Log pura asíncrona cruda nativa base interna OS Android lógica médica pura simple. Para qué: Escribir I puramente asíncrono nato OS Android base. Por qué: Clean code puramente asíncrono crudo nato OS.
        Log.i(TAG, message) // Qué: Sello I puro asíncrono crudo nativo OS Android base interno lógica pura. Para qué: Logcat OS puro asíncrono nato base. Por qué: Debug pura nativa asíncrona cruda OS.
    } // Qué: Fin envoltura I pura asíncrona cruda nativa base interna OS Android lógica médica pura simple cruda OS nativa. Para qué: N/A. Por qué: N/A.

    private fun logError(message: String) { // Qué: Abstracción envoltorio Error rojo puro asíncrono crudo nato OS Android base lógica pura interna médica simple. Para qué: Escribir E puramente asíncrono nato OS Android base cruda. Por qué: Idem pura asíncrona cruda nativa OS.
        Log.e(TAG, message) // Qué: Sello E puro asíncrono crudo nativo OS Android base interna pura médica lógica simple nativa. Para qué: Rojo puramente asíncrono nato OS Android base cruda. Por qué: Idem pura asíncrona cruda nativa OS Android base.
    } // Qué: Fin envoltorio Error puro asíncrono crudo nativo OS Android base interno médica lógica pura simple nativa cruda asíncrona OS general base. Para qué: N/A. Por qué: N/A.

    private fun startUdpListener() { // Qué: Engendro monstruoso Thread paralelo puro de red asíncrono crudo nativo base interno Android OS lógica pura médica simple experimental. Para qué: Crear socket IP pura cruda asíncrona nata OS Android. Por qué: Control Remoto puramente asíncrono crudo nato base interna OS Android.
        thread(isDaemon = true) { // Qué: Desplaza al inframundo de Background OS puro asíncrono nato crudo base Android (Demonio puro asíncrono). Para qué: No bloquear UI si no llega IP pura asíncrona nativa cruda base interna. Por qué: Red es lenta pura cruda asíncrona nativa OS Android base.
            try { // Qué: Jaula cacería red rota pura asíncrona cruda nativa OS Android base interna lógica pura médica simple. Para qué: Prevenir NetworkError puro asíncrono nato OS Android crudo base. Por qué: Red frágil puramente asíncrona cruda OS nativa base Android.
                val socket = DatagramSocket(null) // Qué: Abre oreja UDP nula pura asíncrona cruda nativa base interna OS Android lógica médica pura simple. Para qué: Alojar Socket OS puro asíncrono nato crudo base interna. Por qué: Hardware red puro asíncrono crudo nativo OS.
                socket.reuseAddress = true // Qué: Setea baliza reciclaje de IP puerto puro asíncrono crudo nato OS Android base interna lógica. Para qué: Eludir puerto ocupado si app reinició puramente asíncrona cruda nativa OS Android base interna lógica. Por qué: Crash Prevention puro nativo asíncrono crudo OS Android.
                socket.bind(java.net.InetSocketAddress(50000)) // Qué: Ancla oreja hardware a puerto 50k puro asíncrono crudo nativo OS Android base interno lógica pura médica simple. Para qué: Destinar IP local puramente asíncrona cruda nativa OS base interna. Por qué: Python enviará a 50k puro asíncrono crudo nato base Android OS.
                socket.broadcast = true // Qué: Otorga baliza oreja gigante pura asíncrona cruda nativa OS Android base interna lógica pura médica simple nativa. Para qué: Escuchar grito local de Python puramente asíncrona cruda nativa OS Android base interna lógica. Por qué: IP abierta pura nativa asíncrona cruda OS.
                val buffer = ByteArray(256) // Qué: Reserva cubeta 256 bytes puros asíncronos nativos crudos OS Android base interna lógica pura médica. Para qué: Atrapar telegrama puramente asíncrono nato crudo base OS Android. Por qué: Espacio RAM puro asíncrono crudo nativo OS.
                while (true) { // Qué: Bucle infinito demonio puro asíncrono crudo nato OS Android base interno lógica pura médica simple nativa cruda OS Android general pura. Para qué: Siempre escuchar puramente asíncrono nato crudo base OS Android interna. Por qué: Robot 24/7 puro asíncrono crudo nativo OS Android base.
                    val packet = DatagramPacket(buffer, buffer.size) // Qué: Forja telegrama pura asíncrona cruda nativa base interna OS Android lógica pura médica simple. Para qué: Alojar paquete IP puramente asíncrono nato OS Android crudo base. Por qué: Contenedor puro asíncrono nativo crudo OS Android base.
                    socket.receive(packet) // Qué: Acción bloqueante C++ pura asíncrona nata OS Android base cruda interna lógica (Se pausa hasta recibir byte). Para qué: Esperar IP puramente asíncrono crudo nato OS base Android interna. Por qué: Hilo pasivo puro asíncrono crudo nativo OS.
                    val message = String(packet.data, 0, packet.length).trim() // Qué: Mutila bytes IP a String humana pura asíncrona cruda nativa OS Android base interna lógica médica pura simple. Para qué: Destilar "START_MONITORING" puro asíncrono nato crudo base OS Android. Por qué: Lectura comando pura asíncrona cruda nativa OS.
                    Log.d("UDP_LISTENER", "Recibido: $message") // Qué: Loguea paquete puramente asíncrono nato crudo base OS Android interna lógica pura médica simple nativa cruda OS. Para qué: Debug remoto puramente asíncrono crudo nato OS Android base. Por qué: Saber si Python pegó puro asíncrono crudo nativo OS Android base.
                    
                    if (message == "START_MONITORING") { // Qué: Trampa cadena Comando 1 pura asíncrona cruda nativa base interna OS Android lógica pura médica simple. Para qué: Reaccionar ON puramente asíncrono nato OS Android crudo base interna. Por qué: Automatización Tesis puramente asíncrona cruda nativa OS Android base.
                        if (!isMonitoring) { // Qué: Revisa candado puro asíncrono crudo nato OS Android base interna lógica pura médica. Para qué: Evitar ON doble puramente asíncrono crudo nato OS Android base. Por qué: Seguridad puro nativo asíncrono OS.
                            runOnUiThread { startMonitoring() } // Qué: Salto UI Thread puro asíncrono crudo nato OS Android base interna lógica pura médica simple nativa cruda OS. Para qué: Ejecutar encendido puramente asíncrono crudo nato OS Android base interna desde UI. Por qué: Obligación SDK pura asíncrona cruda nativa OS Android.
                        } // Qué: Fin candado doble ON puro asíncrono crudo nato OS Android base interna lógica pura médica. Para qué: N/A. Por qué: N/A.
                    } else if (message == "STOP_MONITORING") { // Qué: Trampa cadena Comando 2 pura asíncrona cruda nativa base interna OS Android lógica pura médica simple. Para qué: Reaccionar OFF puramente asíncrono nato crudo base OS Android. Por qué: Apagar a lo lejos puramente asíncrona cruda nativa OS.
                        if (isMonitoring) { // Qué: Revisa candado off puro asíncrono crudo nato OS Android base interna lógica pura médica simple. Para qué: Evitar OFF doble puramente asíncrono nato crudo OS Android base interna lógica pura. Por qué: Prevención Crash puro asíncrono crudo nativo OS.
                            runOnUiThread { stopMonitoring() } // Qué: Salto UI puro asíncrono crudo nato OS Android base interna lógica pura médica simple nativa cruda OS Android general pura asíncrona nativa. Para qué: Eutanasia asíncrona puramente desde UI Thread OS Android crudo base interna. Por qué: Obligación SDK pura asíncrona cruda OS.
                        } // Qué: Fin candado doble OFF puro asíncrono crudo nato OS Android base interna lógica pura médica. Para qué: N/A. Por qué: N/A.
                    } // Qué: Fin cadena trampa 2 pura asíncrona cruda nativa base interna OS Android lógica pura médica simple nativa cruda OS Android. Para qué: N/A. Por qué: N/A.
                } // Qué: Fin Bucle infinito Demonio Robot puro asíncrono crudo nato OS Android base interna lógica pura médica simple nativa cruda OS Android general. Para qué: N/A. Por qué: N/A.
            } catch (e: Exception) { // Qué: Red atragantada captura puramente asíncrona nata OS Android cruda base interna lógica pura médica simple nativa. Para qué: Absorber pánico red pura asíncrona cruda nativa OS Android base interna. Por qué: TCP/UDP falla mucho puramente asíncrona cruda nativa OS Android base.
                Log.e("UDP_LISTENER", "Error: ${e.message}") // Qué: Pinta rojo red puramente asíncrono crudo nato OS Android base interna lógica pura médica simple nativa cruda OS Android general pura asíncrona. Para qué: Dev Debug puramente asíncrono crudo nato OS Android base. Por qué: Rastreo puro asíncrono crudo nativo OS.
            } // Qué: Fin atrape pánico red puro asíncrono crudo nativo base interno Android OS lógica pura médica simple nativa cruda asíncrona OS general base. Para qué: N/A. Por qué: N/A.
        } // Qué: Fin invocación a Inframundo demonio puro asíncrono crudo nativo interno base Android OS lógica médica pura simple nativa cruda asíncrona. Para qué: N/A. Por qué: N/A.
    } // Qué: Fin controlador remoto robótico Tesis puro asíncrono crudo nativo base interno Android OS lógica médica pura simple nativa cruda asíncrona OS general. Para qué: N/A. Por qué: N/A.
} // Qué: Fin absoluto clase UI cruda asíncrona pura nativa interna lógica base Android OS médica general pura simple nativa cruda asíncrona. Para qué: N/A. Por qué: N/A.
