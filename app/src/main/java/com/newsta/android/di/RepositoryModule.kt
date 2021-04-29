package com.newsta.android.di

import com.newsta.android.data.local.StoriesDAO
import com.newsta.android.remote.services.AuthenticationService
import com.newsta.android.remote.services.NewsService
import com.newsta.android.repository.AuthRepository
import com.newsta.android.repository.StoryRepository
import com.newsta.android.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAuthRepostory(authenticationService: AuthenticationService): AuthRepository{
        return AuthRepository(authenticationService)
    }


    @Singleton
    @Provides
    fun provideStoriesRepostory(newsService: NewsService, dao: StoriesDAO): StoryRepository{
        return StoryRepository(dao, newsService)
    }

    @Singleton
    @Provides
    fun provideNewsRepository(newsService: NewsService): NewsRepository {
        return NewsRepository(newsService)
    }

}