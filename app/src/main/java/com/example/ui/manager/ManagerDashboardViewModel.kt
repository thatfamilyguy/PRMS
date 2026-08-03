package com.example.ui.manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Patient
import com.example.data.model.Vitals
import com.example.data.repository.PrmsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ManagerDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PrmsRepository.getInstance(application)

    val patients: StateFlow<List<Patient>> = repository.allPatients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentVitals: StateFlow<List<Vitals>> = repository.recentVitals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}
