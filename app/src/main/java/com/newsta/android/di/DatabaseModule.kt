package com.newsta.android.di

/*
import android.content.Context
import androidx.room.Room
import com.newsta.android.data.local.Converters
import com.newsta.android.data.local.StoriesDAO
import com.newsta.android.data.local.StoriesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStoriesDatabase(@ApplicationContext context: Context): StoriesDatabase {
        return Room.databaseBuilder(
            context,
            StoriesDatabase::class.java,
            "newsta_database"
        )
            .addTypeConverter(Converters())
            .build()
    }

    @Provides
    @Singleton
    fun provideStoriesDAO(storiesDatabase: StoriesDatabase): StoriesDAO {
        return storiesDatabase.storiesDao()
    }

}*/
