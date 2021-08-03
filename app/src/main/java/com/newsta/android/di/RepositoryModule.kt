package com.newsta.android.di

import com.newsta.android.data.local.StoriesDAO
import com.newsta.android.remote.services.AuthenticationService
import com.newsta.android.remote.services.NewsService
import com.newsta.android.repository.AuthRepository
import com.newsta.android.repository.StoriesRepository
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
    fun provideAuthRepository(authenticationService: AuthenticationService, dao: StoriesDAO): AuthRepository{
        return AuthRepository(authenticationService, dao)
    }

    @Singleton
    @Provides
    fun provideStoriesRepository(newsService: NewsService, dao: StoriesDAO): StoriesRepository{
        return StoriesRepository(dao, newsService)
    }

}