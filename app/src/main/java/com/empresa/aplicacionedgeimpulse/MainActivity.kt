package com.empresa.aplicacionedgeimpulse

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var isMonitoring = false

    private lateinit var etPhone: EditText
    private lateinit var btnToggleMonitor: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvPrediction: TextView

    // Configuración de Edge Impulse
    private val bufferSize = 300 // EI_CLASSIFIER_DSP_INPUT_FRAME_SIZE (100 samples * 3 axes)
    private val featuresBuffer = FloatArray(bufferSize)
    private var bufferIndex = 0

    private val FALL_THRESHOLD = 0.85f // Umbral de confianza
    private var isAlertActive = false
    
    // Clases que representan caídas
    private val FALL_CLASSES = listOf(
        "fall_backward",
        "fall_bending",
        "fall_forward",
        "fall_hand",
        "fall_sideward_left",
        "fall_sideward_right",
        "fall_sitting",
        "fall_syncope"
    )

    // Diccionario de traducciones para la interfaz de usuario
    private val labelDisplayNames = mapOf(
        "fall_backward"       to "Caída hacia atrás",
        "fall_bending"        to "Caída agachándose",
        "fall_forward"        to "Caída hacia adelante",
        "fall_hand"           to "Caída de manos",
        "fall_sideward_left"  to "Caída lateral izquierda",
        "fall_sideward_right" to "Caída lateral derecha",
        "fall_sitting"        to "Caída sentado",
        "fall_syncope"        to "Síncope / Desmayo",
        "walk"                to "Caminando",
        "stand"               to "De pie",
        "sit"                 to "Sentado",
        "idle"                to "Sin movimiento",
        "normal"              to "Normal",
        "running"             to "Corriendo"
    )

    companion object {
        private const val TAG = "EdgeImpulseAppLogs"
        private const val PERMISSION_REQUEST_CODE = 101
        private const val REQUEST_CODE_ALERT = 102

        init {
            System.loadLibrary("aplicacionedgeimpulse")
        }
    }

    external fun runClassification(features: FloatArray): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etPhone = findViewById(R.id.etPhone)
        btnToggleMonitor = findViewById(R.id.btnToggleMonitor)
        tvStatus = findViewById(R.id.tvStatus)
        tvPrediction = findViewById(R.id.tvPrediction)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Validación de 10 dígitos y solo números
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length > 10) {
                    s.delete(10, s.length) // Restringir a 10 dígitos
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        checkPermissions()

        btnToggleMonitor.setOnClickListener {
            val phone = etPhone.text.toString()
            if (phone.length != 10) {
                Toast.makeText(this, "Ingresa un número válido de 10 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isMonitoring) {
                stopMonitoring()
            } else {
                startMonitoring()
            }
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
        )
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    private fun startMonitoring() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            isMonitoring = true
            isAlertActive = false
            btnToggleMonitor.text = "Detener Monitoreo"
            tvStatus.text = "Monitoreando..."
            bufferIndex = 0
            logInfo("Monitoreo iniciado.")
        } ?: logError("Acelerómetro no disponible.")
    }

    private fun stopMonitoring() {
        sensorManager.unregisterListener(this)
        isMonitoring = false
        btnToggleMonitor.text = "Iniciar Monitoreo"
        tvStatus.text = "Detenido"
        logInfo("Monitoreo detenido.")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !isMonitoring || isAlertActive) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            featuresBuffer[bufferIndex++] = event.values[0]
            featuresBuffer[bufferIndex++] = event.values[1]
            featuresBuffer[bufferIndex++] = event.values[2]

            if (bufferIndex >= bufferSize) {
                bufferIndex = 0
                performInference()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun performInference() {
        val resultString = runClassification(featuresBuffer)

        if (resultString.startsWith("ERROR")) {
            logError("Fallo en inferencia: $resultString")
            return
        }

        val parts = resultString.split("|")
        if (parts.size == 2) {
            // Limpieza como estaba en la versión de 9 clases original
            val label = parts[0].replace("\u0000", "").trim()
            val confidenceStr = parts[1].replace("\u0000", "").trim().replace(",", ".")
            val confidence = confidenceStr.toFloatOrNull() ?: 0f

            val percentage = (confidence * 100).roundToInt()
            val translatedLabel = labelDisplayNames[label] ?: label

            runOnUiThread {
                tvPrediction.text = "Predicción: $translatedLabel ($percentage%)"
            }

            logInfo("Inferencia completada: $label ($percentage%)")

            if (FALL_CLASSES.contains(label) && confidence >= FALL_THRESHOLD) {
                logInfo("Posible caída detectada ($label). Lanzando AlertActivity.")
                startFallAlert(translatedLabel)
            }
        }
    }

    private fun startFallAlert(fallType: String) {
        isAlertActive = true
        val phone = etPhone.text.toString().trim()
        
        val intent = Intent(this, AlertActivity::class.java).apply {
            putExtra(AlertActivity.EXTRA_PHONE, phone)
            putExtra(AlertActivity.EXTRA_FALL_TYPE, fallType)
        }
        startActivityForResult(intent, REQUEST_CODE_ALERT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ALERT) {
            isAlertActive = false
            bufferIndex = 0
            if (isMonitoring) {
                tvStatus.text = "Monitoreando..."
            }
        }
    }

    // Funciones de Logs
    private fun logInfo(message: String) {
        Log.i(TAG, message)
    }

    private fun logError(message: String) {
        Log.e(TAG, message)
    }
}