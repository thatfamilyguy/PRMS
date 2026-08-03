package com.example.data.db

import androidx.room.*
import com.example.data.model.Vitals
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalsDao {
    @Query("SELECT * FROM vitals WHERE patient_id = :patientId ORDER BY timestamp DESC")
    fun getVitalsForPatient(patientId: Long): Flow<List<Vitals>>

    @Query("SELECT * FROM vitals ORDER BY timestamp DESC LIMIT 50")
    fun getRecentVitals(): Flow<List<Vitals>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitals(vitals: Vitals): Long

    @Query("SELECT * FROM vitals WHERE patient_id = :patientId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestVitalForPatient(patientId: Long): Vitals?
}
