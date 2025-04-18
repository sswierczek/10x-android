package com.example.a10xandroid.data.openrouter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendedMovieDTO(
    val id: String = "",
    val tmdbId: String = "",
    val title: String = "",
    val overview: String = "",
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String = "",
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    val reason: String = ""
)
