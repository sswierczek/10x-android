package com.example.a10xandroid.data.service

import com.example.a10xandroid.data.model.MovieList
import com.example.a10xandroid.data.repository.AuthRepository
import com.example.a10xandroid.data.repository.MovieListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val authRepository: AuthRepository,
    private val movieListRepository: MovieListRepository
) {
    val currentUser = authRepository.currentUser

    fun getUserLists(): Flow<List<MovieList>> = currentUser.flatMapLatest { user ->
        user?.let { movieListRepository.getUserLists(it.uid) } ?: flowOf(emptyList())
    }

    suspend fun signIn(email: String, password: String) = authRepository.signIn(email, password)

    suspend fun signUp(email: String, password: String) = authRepository.signUp(email, password)

    suspend fun signOut() = authRepository.signOut()

    suspend fun resetPassword(email: String) = authRepository.resetPassword(email)

    suspend fun updateProfile(displayName: String?, photoUrl: String?) =
        authRepository.updateProfile(
            displayName,
            photoUrl
        )

    suspend fun refreshUserData() = authRepository.refreshUserData()
}
