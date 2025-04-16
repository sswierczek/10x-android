package com.example.a10xandroid.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a10xandroid.data.model.User
import com.example.a10xandroid.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private const val TAG = "ProfileViewModel"

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null,
    val isEditing: Boolean = false,
    val displayName: String = "",
    val photoUrl: String? = null,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,
    val updateError: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "ProfileViewModel initialized")
        loadUserProfile()
    }

    fun loadUserProfile() {
        Log.d(TAG, "Loading user profile")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Get current user from auth repository
                val currentUser = authRepository.currentUser.first()

                if (currentUser != null) {
                    Log.d(TAG, "User profile loaded: ${currentUser.uid}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = currentUser,
                            displayName = currentUser.displayName ?: "",
                            photoUrl = currentUser.photoUrl
                        )
                    }
                } else {
                    Log.e(TAG, "No user found")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No user found. Please sign in again."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user profile", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load profile: ${e.message}"
                    )
                }
            }
        }
    }

    fun startEditing() {
        _uiState.update { it.copy(isEditing = true) }
    }

    fun cancelEditing() {
        _uiState.update {
            it.copy(
                isEditing = false,
                displayName = it.user?.displayName ?: "",
                photoUrl = it.user?.photoUrl
            )
        }
    }

    fun updateDisplayName(name: String) {
        _uiState.update { it.copy(displayName = name) }
    }

    fun updatePhotoUrl(url: String?) {
        _uiState.update { it.copy(photoUrl = url) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdating = true,
                    updateError = null,
                    updateSuccess = false
                )
            }

            try {
                val result = authRepository.updateProfile(
                    displayName = _uiState.value.displayName,
                    photoUrl = _uiState.value.photoUrl
                )

                if (result.isSuccess) {
                    Log.d(TAG, "Profile updated successfully")
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            isEditing = false,
                            updateSuccess = true
                        )
                    }
                    // Reload user profile to get updated data
                    loadUserProfile()
                } else {
                    val exception = result.exceptionOrNull()
                    Log.e(TAG, "Failed to update profile", exception)
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateError = exception?.message ?: "Failed to update profile"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating profile", e)
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        updateError = "Error updating profile: ${e.message}"
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val result = authRepository.signOut()
                if (result.isSuccess) {
                    Log.d(TAG, "User signed out successfully")
                    // Navigation will be handled by the NavGraph
                } else {
                    val exception = result.exceptionOrNull()
                    Log.e(TAG, "Failed to sign out", exception)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception?.message ?: "Failed to sign out"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error signing out", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error signing out: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, updateError = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(updateSuccess = false) }
    }

    fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return "N/A"
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
