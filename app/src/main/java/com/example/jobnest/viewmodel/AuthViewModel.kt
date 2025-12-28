package com.example.jobnest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobnest.data.User
import com.example.jobnest.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val currentUserData: User? = null,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            val user = repository.currentUser
            _authState.value = _authState.value.copy(
                currentUser = user,
                isAuthenticated = user != null
            )
            if (user != null) {
                loadUserData()
            }
        }
    }
    
    fun signUp(email: String, password: String, fullName: String, userType: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = repository.signUp(email, password, fullName, userType)
            result.onSuccess { user ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    currentUserData = user,
                    isAuthenticated = true,
                    currentUser = repository.currentUser
                )
            }.onFailure { exception ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Sign up failed"
                )
            }
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = repository.signIn(email, password)
            result.onSuccess { user ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    currentUser = user,
                    isAuthenticated = true
                )
                loadUserData()
            }.onFailure { exception ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Sign in failed"
                )
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true)
            val result = repository.signOut()
            result.onSuccess {
                _authState.value = AuthState(isAuthenticated = false)
            }.onFailure { exception ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Sign out failed"
                )
            }
        }
    }
    
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = repository.resetPassword(email)
            result.onSuccess {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = null
                )
            }.onFailure { exception ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Password reset failed"
                )
            }
        }
    }
    
    private fun loadUserData() {
        viewModelScope.launch {
            val result = repository.getCurrentUserData()
            result.onSuccess { user ->
                _authState.value = _authState.value.copy(currentUserData = user)
            }
        }
    }
    
    fun updateUserProfile(user: User) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = repository.updateUserProfile(user)
            result.onSuccess {
                loadUserData()
            }.onFailure { exception ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Update failed"
                )
            }
        }
    }
    
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}

