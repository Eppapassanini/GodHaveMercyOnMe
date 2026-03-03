package com.example.tvmazeshows.domain.model

data class Show(
    val id: Int,
    val name: String,
    val summary: String?,
    val imageUrl: String?,
    val genres: List<String>,
    val rating: Double?,
    val networkName: String?,
    val externalLinks: ExternalLinks,
    val url: String,
    val officialSite: String?,
    val premiered: String?,
    val status: String?,
    val language: String?,
)

data class ExternalLinks(
    val tvrage: Int?,
    val thetvdb: Int?,
    val imdb: String?
)


fun com.example.tvmazeshows.data.dto.ShowDto.toDomain(): Show {
    val cleanSummary = summary?.replace(Regex("<[^>]*>"), "")?.trim()
    return Show(
        id = id,
        name = name,
        summary = cleanSummary,
        imageUrl = image?.medium,
        genres = genres,
        rating = rating?.average,
        networkName = network?.name ?: webChannel?.name,
        externalLinks = externals?.toExternalLinks() ?: ExternalLinks(null, null, null),
        url = url,
        officialSite = officialSite,
        premiered = premiered,
        status = status,
        language = language,
    )
}

fun com.example.tvmazeshows.data.dto.ExternalsDto.toExternalLinks(): ExternalLinks {
    return ExternalLinks(
        tvrage = tvrage,
        thetvdb = thetvdb,
        imdb = imdb
    )
}