package com.empresa.aplicacionedgeimpulse

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.empresa.aplicacionedgeimpulse.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var isMonitoring = false

    private val PERMISSION_REQUEST_CODE = 100

    // Búfer para almacenar los datos de X, Y, Z antes de enviarlos a Edge Impulse
    private val sensorDataBuffer = mutableListOf<Float>()
    // La cantidad máxima de muestras depende de la configuración de tu modelo (ventana x frecuencia)
    private val MAX_SAMPLES = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        requestPermissions()

        binding.btnStartMonitoring.setOnClickListener {
            if (isMonitoring) {
                stopMonitoring()
            } else {
                val number = binding.etPhoneNumber.text.toString()
                if (number.length == 10) {
                    startMonitoring()
                } else {
                    Toast.makeText(this, "Ingresa un número de 10 dígitos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestPermissions() {
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
            // SENSOR_DELAY_GAME equivale a ~50Hz. Ajusta según la frecuencia de muestreo de tu modelo.
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            isMonitoring = true
            binding.btnStartMonitoring.text = "Detener Monitoreo"
            binding.tvStatus.text = "Estado: Monitoreando..."
            sensorDataBuffer.clear()
        } ?: Toast.makeText(this, "Acelerómetro no disponible", Toast.LENGTH_SHORT).show()
    }

    private fun stopMonitoring() {
        sensorManager.unregisterListener(this)
        isMonitoring = false
        binding.btnStartMonitoring.text = "Iniciar Monitoreo"
        binding.tvStatus.text = "Estado: Inactivo"
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            sensorDataBuffer.add(event.values[0])
            sensorDataBuffer.add(event.values[1])
            sensorDataBuffer.add(event.values[2])

            if (sensorDataBuffer.size >= MAX_SAMPLES) {
                // Se pasa el arreglo de datos a la función nativa de C++
                val result = runInference(sensorDataBuffer.toFloatArray())
                sensorDataBuffer.clear()

                // Validar la salida nativa. Asegúrate que coincida con la etiqueta de tu modelo.
                if (result.contains("caida", ignoreCase = true)) {
                    triggerEmergency()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun triggerEmergency() {
        stopMonitoring()
        val number = binding.etPhoneNumber.text.toString()
        binding.tvStatus.text = "Estado: ¡Caída detectada! Alerta enviada."

        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(number, null, "Alerta: Posible caída detectada.", null, null)
            Toast.makeText(this, "SMS enviado a $number", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al enviar SMS", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Método que llama a la librería en C++ pasándole el búfer del acelerómetro
     */
    external fun runInference(data: FloatArray): String

    companion object {
        init {
            System.loadLibrary("aplicacionedgeimpulse")
        }
    }
}