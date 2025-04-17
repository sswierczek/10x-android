package com.example.a10xandroid.util

/**
 * A sealed class representing a result that can be either success or error.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Executes the given block if this is a [Success].
     */
    inline fun onSuccess(block: (T) -> Unit): Result<T> {
        if (this is Success) {
            block(data)
        }
        return this
    }

    /**
     * Executes the given block if this is an [Error].
     */
    inline fun onError(block: (Exception) -> Unit): Result<T> {
        if (this is Error) {
            block(exception)
        }
        return this
    }

    /**
     * Returns the encapsulated value if this instance represents success or null if it is error.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    /**
     * Returns the encapsulated value if this instance represents success or throws the encapsulated exception if it is error.
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
    }

    /**
     * Transforms the result using the given transform function.
     */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (Exception) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onFailure(exception)
    }
} 