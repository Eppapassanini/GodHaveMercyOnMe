package com.example.tvmazeshows.data.network

import com.example.tvmazeshows.data.dto.SearchResultDto
import com.example.tvmazeshows.data.dto.ShowDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApi {

    @GET("shows")
    suspend fun getShows(@Query("page") page: Int = 0): List<ShowDto>

    @GET("shows/{id}")
    suspend fun getShowById(@Path("id") showId: Int): ShowDto

    @GET("search/shows")
    suspend fun searchShows(@Query("q") query: String): List<SearchResultDto>
}