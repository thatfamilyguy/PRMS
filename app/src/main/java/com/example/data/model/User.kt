package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "username")
    val username: String,
    
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    
    @ColumnInfo(name = "salt")
    val salt: String,
    
    @ColumnInfo(name = "full_name")
    val fullName: String,
    
    @ColumnInfo(name = "role")
    val role: UserRole,
    
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "department")
    val department: String,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "biometric_enabled")
    val biometricEnabled: Boolean = false,

    @ColumnInfo(name = "last_password_change")
    val lastPasswordChange: Long = System.currentTimeMillis()
) {
    fun isPasswordExpired(expirationPeriodMs: Long = 7L * 24 * 60 * 60 * 1000): Boolean {
        return (System.currentTimeMillis() - lastPasswordChange) >= expirationPeriodMs
    }
}
