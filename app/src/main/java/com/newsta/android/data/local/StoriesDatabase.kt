package com.newsta.android.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.newsta.android.utils.models.Story
import com.newsta.android.utils.models.Event

@Database(entities = [Story::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StoriesDatabase : RoomDatabase() {

    abstract fun storiesDao(): StoriesDAO

    companion object {
        val DATABASE_NAME = "newsta_database"
    }

}
