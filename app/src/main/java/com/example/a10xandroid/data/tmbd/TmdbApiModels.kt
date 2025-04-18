package com.example.a10xandroid.data.tmbd

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for movie search results from TMDB API.
 */
@Serializable
data class TmdbMovieSearchApiResponse(
    val page: Int,
    val results: List<TmdbMovieApiResult>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)

/**
 * Movie result from search or list endpoints from TMDB API.
 */
@Serializable
data class TmdbMovieApiResult(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("vote_average")
    val voteAverage: Double,
    @SerialName("vote_count")
    val voteCount: Int,
    val popularity: Double,
    @SerialName("genre_ids")
    val genreIds: List<Int>
)

/**
 * Detailed movie information from TMDB API.
 */
@Serializable
data class TmdbMovieDetailsApiResponse(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("vote_average")
    val voteAverage: Double,
    @SerialName("vote_count")
    val voteCount: Int,
    val popularity: Double,
    val runtime: Int?,
    val genres: List<TmdbGenreApiResponse>,
    val status: String,
    @SerialName("original_language")
    val originalLanguage: String,
    val budget: Long,
    val revenue: Long,
    val homepage: String?,
    val tagline: String?,
    @SerialName("production_companies")
    val productionCompanies: List<TmdbProductionCompanyApiResponse>,
    @SerialName("spoken_languages")
    val spokenLanguages: List<TmdbSpokenLanguageApiResponse>
)

/**
 * Genre information from TMDB API.
 */
@Serializable
data class TmdbGenreApiResponse(
    val id: Int,
    val name: String
)

/**
 * Production company information from TMDB API.
 */
@Serializable
data class TmdbProductionCompanyApiResponse(
    val id: Int,
    val name: String,
    @SerialName("logo_path")
    val logoPath: String?,
    @SerialName("origin_country")
    val originCountry: String = "",
    @SerialName("parent_company")
    val parentCompany: String? = null
)

/**
 * Spoken language information from TMDB API.
 */
@Serializable
data class TmdbSpokenLanguageApiResponse(
    @SerialName("iso_639_1")
    val iso6391: String,
    val name: String
)
