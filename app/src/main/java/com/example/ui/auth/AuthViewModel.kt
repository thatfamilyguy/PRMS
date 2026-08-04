package com.example.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.PrmsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class PasswordExpired(val user: User, val reason: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PrmsRepository.getInstance(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

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
                    if (user.isPasswordExpired()) {
                        val daysOld = (System.currentTimeMillis() - user.lastPasswordChange) / (1000 * 60 * 60 * 24)
                        _uiState.value = AuthUiState.PasswordExpired(
                            user = user,
                            reason = "Your password is $daysOld days old. Security policy requires changing passwords every 7 days (weekly rotation)."
                        )
                    } else {
                        _uiState.value = AuthUiState.Success(user)
                    }
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
                    if (user.isPasswordExpired()) {
                        val daysOld = (System.currentTimeMillis() - user.lastPasswordChange) / (1000 * 60 * 60 * 24)
                        _uiState.value = AuthUiState.PasswordExpired(
                            user = user,
                            reason = "Your password is $daysOld days old. Security policy requires changing passwords every 7 days (weekly rotation)."
                        )
                    } else {
                        _uiState.value = AuthUiState.Success(user)
                    }
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Preset login failed.")
                }
            )
        }
    }

    fun updatePassword(username: String, currentPassword: String, newPassword: String) {
        if (username.isBlank() || currentPassword.isBlank() || newPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("All password fields are required.")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.updatePassword(username, currentPassword, newPassword)
            result.fold(
                onSuccess = { updatedUser ->
                    _uiState.value = AuthUiState.Success(updatedUser)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Failed to update password.")
                }
            )
        }
    }

    fun simulateExpiredPassword(username: String) {
        val target = if (username.isBlank()) "admin" else username.trim()
        viewModelScope.launch {
            repository.forceExpirePasswordForTesting(target)
            _uiState.value = AuthUiState.Error("Simulated 8-day old password for '$target'. Please click 'Secure Authenticate' to trigger weekly rotation enforcement.")
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
