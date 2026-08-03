package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "actor_username")
    val actorUsername: String,
    
    @ColumnInfo(name = "actor_role")
    val actorRole: UserRole,
    
    @ColumnInfo(name = "action_type")
    val actionType: String, // LOGIN_SUCCESS, BIOMETRIC_AUTH, USER_CREATED, VITALS_ADDED, PATIENT_INTAKE, PRIVILEGE_RESTRICTED
    
    @ColumnInfo(name = "details")
    val details: String,
    
    @ColumnInfo(name = "security_level")
    val securityLevel: String = "INFO" // INFO, WARNING, SECURITY_ALERT
)
