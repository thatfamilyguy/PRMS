package com.example.data.db

import androidx.room.*
import com.example.data.model.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY admission_date DESC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE full_name LIKE '%' || :query || '%' OR mrn LIKE '%' || :query || '%' OR room_number LIKE '%' || :query || '%' ORDER BY full_name ASC")
    fun searchPatients(query: String): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    suspend fun getPatientById(id: Long): Patient?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient): Long

    @Update
    suspend fun updatePatient(patient: Patient)

    @Query("DELETE FROM patients WHERE id = :id")
    suspend fun deletePatient(id: Long)

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int

    @Query("SELECT COUNT(*) FROM patients WHERE status = 'ICU'")
    fun getIcuCount(): Flow<Int>
}
