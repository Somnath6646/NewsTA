package com.newsta.android.di

import com.newsta.android.remote.services.AuthenticationService
import com.newsta.android.repository.AuthRepository
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
}