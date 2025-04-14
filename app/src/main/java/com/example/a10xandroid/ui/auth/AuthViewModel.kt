package com.example.a10xandroid.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.model.User
import com.example.a10xandroid.data.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    val currentUser: Flow<User?> = authService.currentUser

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authService.signIn(email, password)
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authService.signUp(email, password)
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                authService.signOut()
                _uiState.value = AuthUiState.Initial
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Sign out failed")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authService.resetPassword(email)
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Password reset failed")
            }
        }
    }

    fun updateProfile(displayName: String?, photoUrl: String?) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authService.updateProfile(displayName, photoUrl)
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Profile update failed")
            }
        }
    }

    fun resetError() {
        _uiState.value = AuthUiState.Initial
    }
}

sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
} 