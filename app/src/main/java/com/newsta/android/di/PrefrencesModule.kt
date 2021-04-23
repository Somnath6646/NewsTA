package com.newsta.android.di

import android.content.Context
import com.newsta.android.remote.services.AuthenticationService
import com.newsta.android.repository.AuthRepository
import com.newsta.android.utils.prefrences.UserPrefrences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object PrefrencesModule {



    @Singleton
    @Provides
    fun providesUserPrefrences(@ApplicationContext context: Context): UserPrefrences {
        return UserPrefrences(context)
    }
}