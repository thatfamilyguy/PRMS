package com.example.ui.auth

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.PrmsRepository
import com.example.data.security.BiometricAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PrmsRepository.getInstance(application)
    private val biometricAuthManager = BiometricAuthManager(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isBiometricAvailable = MutableStateFlow(biometricAuthManager.isBiometricAvailable())
    val isBiometricAvailable: StateFlow<Boolean> = _isBiometricAvailable.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun login(username: String, passwordAttempt: String) {
        if (username.isBlank() || passwordAttempt.isBlank()) {
            _uiState.value = AuthUiState.Error("Username and password are required.")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.authenticateUser(username, passwordAttempt)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Authentication failed.")
                }
            )
        }
    }

    fun loginWithPreset(role: UserRole) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val (username, pass) = when (role) {
                UserRole.ADMIN -> "admin" to "AdminPassword123!"
                UserRole.MANAGER -> "manager" to "ManagerPassword123!"
                UserRole.NURSE -> "nurse" to "NursePassword123!"
            }
            val result = repository.authenticateUser(username, pass)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Preset login failed.")
                }
            )
        }
    }

    fun triggerBiometricAuth(activity: FragmentActivity, targetUsername: String = "admin") {
        biometricAuthManager.authenticate(
            activity = activity,
            title = "Biometric RBAC Authentication",
            subtitle = "Place finger or scan face to unlock PRMS Medical",
            onSuccess = {
                viewModelScope.launch {
                    val result = repository.authenticateWithBiometrics(targetUsername)
                    result.fold(
                        onSuccess = { user -> _uiState.value = AuthUiState.Success(user) },
                        onFailure = { err -> _uiState.value = AuthUiState.Error(err.message ?: "Biometric login failed.") }
                    )
                }
            },
            onError = { errMsg ->
                _uiState.value = AuthUiState.Error(errMsg)
            }
        )
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
