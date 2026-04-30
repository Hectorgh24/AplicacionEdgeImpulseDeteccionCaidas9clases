package com.empresa.aplicacionedgeimpulse

import android.content.Context
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MonitoringSessionLog(
    val sessionStartMillis: Long,
    val sessionEndMillis: Long? = null,
    val windowsProcessed: Int = 0,
    val fallCount: Int = 0,
    val alertsTriggered: Int = 0,
    val emergencyNumber: String = "",
    val currentPrediction: String = "Inactivo"
) {
    val durationSeconds: Long
        get() = if (sessionEndMillis != null) {
            (sessionEndMillis - sessionStartMillis) / 1000
        } else {
            (System.currentTimeMillis() - sessionStartMillis) / 1000
        }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("sessionStartMillis", sessionStartMillis)
            put("sessionStartIso", isoFormat(sessionStartMillis))
            put("sessionEndMillis", sessionEndMillis ?: JSONObject.NULL)
            put("sessionEndIso", sessionEndMillis?.let { isoFormat(it) } ?: JSONObject.NULL)
            put("durationSeconds", durationSeconds)
            put("windowsProcessed", windowsProcessed)
            put("fallCount", fallCount)
            put("alertsTriggered", alertsTriggered)
            put("emergencyNumber", emergencyNumber)
            put("currentPrediction", currentPrediction)
        }
    }

    companion object {
        private fun isoFormat(timestamp: Long): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            return formatter.format(Date(timestamp))
        }
    }
}

object MonitoringLogManager {
    private const val LOG_FILE_NAME = "monitoring_log.json"
    private const val EXPORT_FILE_NAME = "datos-monitoreo-edge-impulse-9-clases.json"
    private var currentSession: MonitoringSessionLog? = null

    fun startSession(context: Context, emergencyNumber: String) {
        currentSession = MonitoringSessionLog(
            sessionStartMillis = System.currentTimeMillis(),
            emergencyNumber = emergencyNumber
        )
        saveCurrentSession(context)
    }

    fun recordWindow(context: Context) {
        currentSession?.let {
            currentSession = it.copy(windowsProcessed = it.windowsProcessed + 1)
            saveCurrentSession(context)
        }
    }

    fun recordFall(context: Context) {
        currentSession?.let {
            currentSession = it.copy(fallCount = it.fallCount + 1)
            saveCurrentSession(context)
        }
    }

    fun updatePrediction(context: Context, prediction: String) {
        currentSession?.let {
            currentSession = it.copy(currentPrediction = prediction)
            saveCurrentSession(context)
        }
    }

    fun recordAlert(context: Context) {
        currentSession?.let {
            currentSession = it.copy(alertsTriggered = it.alertsTriggered + 1)
            saveCurrentSession(context)
        }
    }

    fun stopSession(context: Context) {
        currentSession?.let {
            currentSession = it.copy(sessionEndMillis = System.currentTimeMillis())
            saveCurrentSession(context)
        }
    }

    fun getCurrentSession(): MonitoringSessionLog? = currentSession

    fun loadLastSession(context: Context): MonitoringSessionLog? {
        val file = File(context.filesDir, LOG_FILE_NAME)
        if (!file.exists()) return null
        return try {
            val json = JSONObject(file.readText())
            MonitoringSessionLog(
                sessionStartMillis = json.optLong("sessionStartMillis"),
                sessionEndMillis = if (json.isNull("sessionEndMillis")) null else json.optLong("sessionEndMillis"),
                windowsProcessed = json.optInt("windowsProcessed"),
                fallCount = json.optInt("fallCount"),
                alertsTriggered = json.optInt("alertsTriggered"),
                emergencyNumber = json.optString("emergencyNumber"),
                currentPrediction = json.optString("currentPrediction", "Inactivo")
            )
        } catch (_: Exception) {
            null
        }
    }

    fun exportReportToDownloads(context: Context): String? {
        val session = currentSession ?: loadLastSession(context) ?: return null
        val jsonData = session.toJson().toString(2).toByteArray()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, EXPORT_FILE_NAME)
                put(MediaStore.Downloads.MIME_TYPE, "application/json")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return null
            resolver.openOutputStream(uri)?.use { it.write(jsonData) } ?: return null
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            "Download/$EXPORT_FILE_NAME"
        } else {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            val file = File(downloadDir, EXPORT_FILE_NAME)
            FileOutputStream(file).use { it.write(jsonData) }
            file.absolutePath
        }
    }

    private fun saveCurrentSession(context: Context) {
        currentSession?.let {
            val file = File(context.filesDir, LOG_FILE_NAME)
            file.writeText(it.toJson().toString(2))
        }
    }
}
