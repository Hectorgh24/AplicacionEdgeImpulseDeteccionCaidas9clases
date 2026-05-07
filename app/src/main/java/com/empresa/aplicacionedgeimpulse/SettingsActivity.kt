package com.empresa.aplicacionedgeimpulse

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var tvSessionStart: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvWindows: TextView
    private lateinit var tvFalls: TextView
    private lateinit var tvAlerts: TextView
    private lateinit var tvEmergencyNumber: TextView
    private lateinit var tvPrediction: TextView
    private lateinit var btnExportReport: Button

    private val refreshHandler = Handler(Looper.getMainLooper())
    private val refreshTask = object : Runnable {
        override fun run() {
            renderSession()
            refreshHandler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.title = "Ajustes"

        tvSessionStart = findViewById(R.id.tvSessionStart)
        tvDuration = findViewById(R.id.tvDuration)
        tvWindows = findViewById(R.id.tvWindows)
        tvFalls = findViewById(R.id.tvFalls)
        tvAlerts = findViewById(R.id.tvAlerts)
        tvEmergencyNumber = findViewById(R.id.tvEmergencyNumber)
        tvPrediction = findViewById(R.id.tvPredictionLog)
        btnExportReport = findViewById(R.id.btnExportReport)

        btnExportReport.setOnClickListener {
            val path = MonitoringLogManager.exportReportToDownloads(this)
            if (path != null) {
                Toast.makeText(this, "Reporte guardado en: $path", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        renderSession()
        refreshHandler.post(refreshTask)
    }

    override fun onStop() {
        refreshHandler.removeCallbacks(refreshTask)
        super.onStop()
    }

    private fun renderSession() {
        val session = MonitoringLogManager.getCurrentSession() ?: MonitoringLogManager.loadLastSession(this)
        if (session == null) {
            tvSessionStart.text = "Fecha de inicio: -"
            tvDuration.text = "Duración (segundos): 0"
            tvWindows.text = "Clases procesadas: 0"
            tvFalls.text = "Cantidad de caídas detectadas: 0"
            tvAlerts.text = "Alertas enviadas: 0"
            tvEmergencyNumber.text = "Número de emergencia: -"
            tvPrediction.text = "Última predicción: Inactivo"
            return
        }

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startDate = formatter.format(Date(session.sessionStartMillis))
        tvSessionStart.text = "Fecha de inicio: $startDate"
        tvDuration.text = "Duración (segundos): ${session.durationSeconds}"
        tvWindows.text = "Clases procesadas: ${session.windowsProcessed}"
        tvFalls.text = "Cantidad de caídas detectadas: ${session.fallCount}"
        tvAlerts.text = "Alertas enviadas: ${session.alertsTriggered}"
        tvEmergencyNumber.text = "Número de emergencia: ${session.emergencyNumber.ifBlank { "-" }}"
        tvPrediction.text = "Última predicción: ${session.currentPrediction}"
    }
}
