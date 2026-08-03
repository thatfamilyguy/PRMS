package com.example.data.model

enum class UserRole(val displayName: String, val badgeColorHex: Long) {
    ADMIN("System Admin", 0xFFDC2626),   // Red / High Privilege
    MANAGER("Hospital Manager", 0xFF0284C7), // Blue / Analytics
    NURSE("Clinical Nurse", 0xFF16A34A)   // Green / Clinical Ops
}
