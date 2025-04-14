package com.example.a10xandroid.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.model.User
import com.example.a10xandroid.data.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    val currentUser: Flow<User?> = authService.currentUser

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Observe currentUser changes to update UI state
        viewModelScope.launch {
            currentUser.collect { user ->
                Log.d("AuthViewModel", "Current user changed: ${user != null}")
                if (user != null && _uiState.value is AuthUiState.Loading) {
                    Log.d("AuthViewModel", "Setting UI state to Success due to user change")
                    _uiState.value = AuthUiState.Success
                }
            }
        }
    }

    fun refreshUserData() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                // Force a refresh of the current user data
                authService.refreshUserData()
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(getErrorMessage(e))
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authService.signIn(email, password)
                Log.d("AuthViewModel", "Sign in successful, setting UI state to Success")
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign in failed: ${e.message}")
                _uiState.value = AuthUiState.Error(getErrorMessage(e))
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authService.signUp(email, password)
                Log.d("AuthViewModel", "Sign up successful, refreshing user data")
                // Refresh user data after successful signup
                authService.refreshUserData()
                Log.d("AuthViewModel", "User data refreshed, setting UI state to Success")
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign up failed: ${e.message}")
                _uiState.value = AuthUiState.Error(getErrorMessage(e))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                authService.signOut()
                _uiState.value = AuthUiState.Initial
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(getErrorMessage(e))
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
                _uiState.value = AuthUiState.Error(getErrorMessage(e))
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
                _uiState.value = AuthUiState.Error(getErrorMessage(e))
            }
        }
    }

    fun resetError() {
        _uiState.value = AuthUiState.Initial
    }

    private fun getErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("password") == true -> "Invalid password. Please try again."
            e.message?.contains("email") == true -> "Invalid email address. Please check and try again."
            e.message?.contains("network") == true -> "Network error. Please check your internet connection."
            e.message?.contains("already in use") == true -> "This email is already registered. Please sign in instead."
            e.message?.contains("weak-password") == true -> "Password is too weak. Please use at least 6 characters."
            e.message?.contains("badly formatted") == true -> "Invalid email format. Please enter a valid email address."
            e.message?.contains("user-not-found") == true -> "No account found with this email. Please sign up first."
            e.message?.contains("too many requests") == true -> "Too many attempts. Please try again later."
            else -> e.message ?: "An unexpected error occurred. Please try again."
        }
    }
}

sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
