package com.example.ui.nurse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Patient
import com.example.data.model.User
import com.example.data.model.Vitals
import com.example.data.repository.PrmsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NurseDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PrmsRepository.getInstance(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val patients: StateFlow<List<Patient>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allPatients
            } else {
                repository.patientDao.searchPatients(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun registerPatient(
        nurseUser: User,
        patient: Patient,
        onResult: (Result<Long>) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerPatient(nurseUser, patient)
            onResult(result)
        }
    }

    fun recordVitals(
        nurseUser: User,
        vitals: Vitals,
        onResult: (Result<Long>) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.recordVitals(nurseUser, vitals)
            onResult(result)
        }
    }

    fun getVitalsForPatient(patientId: Long): Flow<List<Vitals>> {
        return repository.vitalsDao.getVitalsForPatient(patientId)
    }
}
