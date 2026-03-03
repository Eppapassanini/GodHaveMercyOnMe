package com.example.tvmazeshows.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("summary") val summary: String?,
    @SerialName("image") val image: ImageDto?,
    @SerialName("genres") val genres: List<String>,
    @SerialName("rating") val rating: RatingDto?,
    @SerialName("network") val network: NetworkDto?,
    @SerialName("webChannel") val webChannel: NetworkDto?,
    @SerialName("externals") val externals: ExternalsDto?,
    @SerialName("url") val url: String,
    @SerialName("officialSite") val officialSite: String?,
    @SerialName("premiered") val premiered: String?,
    @SerialName("status") val status: String?,
    @SerialName("language") val language: String?,
)

@Serializable
data class ImageDto(
    @SerialName("medium") val medium: String?,
    @SerialName("original") val original: String?
)

@Serializable
data class RatingDto(
    @SerialName("average") val average: Double?
)

@Serializable
data class NetworkDto(
    @SerialName("id") val id: Int?,
    @SerialName("name") val name: String?,
    @SerialName("country") val country: CountryDto?
)

@Serializable
data class CountryDto(
    @SerialName("name") val name: String?,
    @SerialName("code") val code: String?
)

@Serializable
data class ExternalsDto(
    @SerialName("tvrage") val tvrage: Int?,
    @SerialName("thetvdb") val thetvdb: Int?,
    @SerialName("imdb") val imdb: String?
)

@Serializable
data class SearchResultDto(
    @SerialName("score") val score: Double,
    @SerialName("show") val show: ShowDto
)