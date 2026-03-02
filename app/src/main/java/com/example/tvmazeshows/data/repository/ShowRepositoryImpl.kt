
package com.example.tvmazeshows.data.repository

import com.example.tvmazeshows.data.network.TvMazeApi
import com.example.tvmazeshows.data.network.IoDispatcher
import com.example.tvmazeshows.domain.model.Show
import com.example.tvmazeshows.domain.model.toDomain
import com.example.tvmazeshows.domain.repository.ShowRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowRepositoryImpl @Inject constructor(
    private val api: TvMazeApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ShowRepository {

    override suspend fun getShows(page: Int): Result<List<Show>> =
        withContext(ioDispatcher) {
            runCatching {
                api.getShows(page).map { it.toDomain() }
            }
        }

    override suspend fun getShowDetails(showId: Int): Result<Show> =
        withContext(ioDispatcher) {
            runCatching {
                api.getShowById(showId).toDomain()
            }
        }

    override suspend fun searchShows(query: String): Result<List<Show>> =
        withContext(ioDispatcher) {
            runCatching {
                api.searchShows(query).map { it.show.toDomain() }
            }
        }
}