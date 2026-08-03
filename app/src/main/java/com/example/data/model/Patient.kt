package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "mrn")
    val mrn: String, // Medical Record Number (e.g., MRN-9021)
    
    @ColumnInfo(name = "full_name")
    val fullName: String,
    
    @ColumnInfo(name = "dob")
    val dob: String,
    
    @ColumnInfo(name = "gender")
    val gender: String,
    
    @ColumnInfo(name = "blood_type")
    val bloodType: String,
    
    @ColumnInfo(name = "room_number")
    val roomNumber: String,
    
    @ColumnInfo(name = "admission_date")
    val admissionDate: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "primary_diagnosis")
    val primaryDiagnosis: String,
    
    @ColumnInfo(name = "attending_physician")
    val attendingPhysician: String,
    
    @ColumnInfo(name = "allergies")
    val allergies: String = "None Known",
    
    @ColumnInfo(name = "status")
    val status: String = "Admitted", // Admitted, ICU, Discharged, Observation
    
    @ColumnInfo(name = "department")
    val department: String = "Cardiology"
)
