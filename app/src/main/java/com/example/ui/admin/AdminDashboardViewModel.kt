package com.example.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuditLog
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.PrmsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PrmsRepository.getInstance(application)

    val users: StateFlow<List<User>> = repository.allUsers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val auditLogs: StateFlow<List<AuditLog>> = repository.auditLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createUser(
        creatorUser: User,
        username: String,
        fullName: String,
        role: UserRole,
        email: String,
        dept: String,
        pass: String,
        onResult: (Result<Long>) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.createUser(creatorUser, username, fullName, role, email, dept, pass)
            onResult(res)
        }
    }

    fun toggleUserActiveState(adminUser: User, targetUser: User) {
        viewModelScope.launch {
            repository.toggleUserActiveState(adminUser, targetUser)
        }
    }
}
