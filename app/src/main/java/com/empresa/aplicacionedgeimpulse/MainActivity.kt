package com.empresa.aplicacionedgeimpulse

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var isMonitoring = false

    private lateinit var etPhone: EditText
    private lateinit var btnToggleMonitor: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvPrediction: TextView

    // Configuración de Edge Impulse
    private val bufferSize = 300 // Cambiar por el valor exacto de EI_CLASSIFIER_DSP_INPUT_FRAME_SIZE de tu modelo
    private val featuresBuffer = FloatArray(bufferSize)
    private var bufferIndex = 0

    // Temporizador de 5 segundos
    private var fallTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private val FALL_THRESHOLD = 0.85f // Umbral de confianza
    private val FALL_CLASSES = listOf("caida_adelante", "caida_atras", "caida_lateral") // Ajustar a los nombres exactos de tus clases de caída

    companion object {
        private const val TAG = "EdgeImpulseAppLogs"
        private const val PERMISSION_REQUEST_CODE = 101

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

        checkPermissions()

        btnToggleMonitor.setOnClickListener {
            val phone = etPhone.text.toString()
            if (phone.isEmpty()) {
                Toast.makeText(this, "Ingresa un número válido", Toast.LENGTH_SHORT).show()
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
        cancelFallTimer()
        logInfo("Monitoreo detenido.")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !isMonitoring) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Edge Impulse generalmente usa m/s^2. Asegúrate de que las unidades coincidan con tus datos de entrenamiento.
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
            val label = parts[0]
            val confidence = parts[1].toFloatOrNull() ?: 0f

            runOnUiThread {
                tvPrediction.text = "Predicción: $label (${String.format("%.2f", confidence)})"
            }

            logInfo("Inferencia completada: $label ($confidence)")

            if (FALL_CLASSES.contains(label) && confidence >= FALL_THRESHOLD) {
                if (!isTimerRunning) {
                    logInfo("Posible caída detectada ($label). Iniciando temporizador de 5 segundos.")
                    startFallTimer(label)
                }
            } else {
                // Opcional: Cancelar el temporizador si se detecta una clase de recuperación o movimiento normal de alta confianza
                // if (label == "recuperacion" && confidence > 0.9f) cancelFallTimer()
            }
        }
    }

    private fun startFallTimer(fallType: String) {
        isTimerRunning = true
        runOnUiThread { tvStatus.text = "¡Alerta! Caída: $fallType en 5s..." }

        fallTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / 1000
                logInfo("Temporizador de caída: ${sec}s restantes.")
                runOnUiThread { tvStatus.text = "¡Alerta en ${sec}s!" }
            }

            override fun onFinish() {
                isTimerRunning = false
                logInfo("Temporizador finalizado. Ejecutando protocolo de emergencia.")
                runOnUiThread { tvStatus.text = "Enviando alerta..." }
                executeEmergencyProtocol(fallType)
            }
        }.start()
    }

    private fun cancelFallTimer() {
        fallTimer?.cancel()
        isTimerRunning = false
        runOnUiThread { if (isMonitoring) tvStatus.text = "Monitoreando..." }
        logInfo("Temporizador de caída cancelado.")
    }

    private fun executeEmergencyProtocol(fallType: String) {
        val phoneNumber = etPhone.text.toString()
        if (phoneNumber.isNotEmpty()) {
            sendSMS(phoneNumber, "Alerta de Emergencia: Se ha detectado una caída del tipo '$fallType'.")
            makeCall(phoneNumber)
        } else {
            logError("Número de teléfono no configurado para alerta.")
        }
        // Reiniciar estado
        if(isMonitoring) {
            runOnUiThread { tvStatus.text = "Monitoreando..." }
        }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            logInfo("SMS enviado a $phoneNumber")
            Toast.makeText(this, "SMS enviado", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            logError("Error al enviar SMS: ${e.message}")
        }
    }

    private fun makeCall(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
            logInfo("Llamada iniciada a $phoneNumber")
        } catch (e: SecurityException) {
            logError("Permiso denegado para realizar llamada.")
        } catch (e: Exception) {
            logError("Error al realizar llamada: ${e.message}")
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