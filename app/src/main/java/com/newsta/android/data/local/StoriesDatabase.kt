package com.newsta.android.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.newsta.android.utils.models.Category
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story
import com.newsta.android.utils.models.UserCategory

@Database(entities = arrayOf(Story::class, SavedStory::class, Category::class, UserCategory::class), version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StoriesDatabase : RoomDatabase() {

    abstract fun storiesDao(): StoriesDAO

    companion object {
        val DATABASE_NAME = "newsta_database"
    }

}
