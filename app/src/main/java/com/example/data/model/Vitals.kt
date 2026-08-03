package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vitals")
data class Vitals(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "patient_id")
    val patientId: Long,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "heart_rate")
    val heartRate: Int, // bpm
    
    @ColumnInfo(name = "blood_pressure")
    val bloodPressure: String, // e.g., "120/80"
    
    @ColumnInfo(name = "spo2")
    val spo2: Int, // % Oxygen
    
    @ColumnInfo(name = "temperature_c")
    val temperatureC: Double, // Celsius
    
    @ColumnInfo(name = "respiratory_rate")
    val respiratoryRate: Int = 16, // breaths/min
    
    @ColumnInfo(name = "nurse_notes")
    val nurseNotes: String = "",
    
    @ColumnInfo(name = "recorded_by")
    val recordedBy: String = "Nurse"
) {
    fun getVitalStatus(): VitalStatus {
        val isHrWarning = heartRate !in 60..100
        val isSpo2Warning = spo2 < 95
        val isTempWarning = temperatureC < 36.1 || temperatureC > 38.0
        
        return when {
            spo2 < 90 || heartRate > 130 || heartRate < 45 || temperatureC > 39.0 -> VitalStatus.CRITICAL
            isHrWarning || isSpo2Warning || isTempWarning -> VitalStatus.WARNING
            else -> VitalStatus.NORMAL
        }
    }
}

enum class VitalStatus(val label: String, val colorHex: Long) {
    NORMAL("Stable", 0xFF16A34A),
    WARNING("Warning", 0xFFD97706),
    CRITICAL("Critical", 0xFFDC2626)
}
