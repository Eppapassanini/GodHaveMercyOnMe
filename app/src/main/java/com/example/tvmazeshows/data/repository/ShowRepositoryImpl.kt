
package com.example.tvmazeshows.di

import com.example.tvmazeshows.data.network.TvMazeApi
import com.example.tvmazeshows.data.network.IoDispatcher
import com.example.tvmazeshows.data.repository.ShowRepositoryImpl
import com.example.tvmazeshows.domain.repository.ShowRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideShowRepository(
        api: TvMazeApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ShowRepository = ShowRepositoryImpl(api, ioDispatcher)
}
