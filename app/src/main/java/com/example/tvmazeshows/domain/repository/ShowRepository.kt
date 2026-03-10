package com.example.tvmazeshows.domain.repository

import com.example.tvmazeshows.domain.model.Show

interface ShowRepository {
    suspend fun getShows(page: Int = 0): Result<List<Show>>
    suspend fun getShowDetails(showId: Int): Result<Show>
}
