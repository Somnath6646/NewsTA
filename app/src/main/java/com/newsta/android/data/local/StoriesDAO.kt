package com.newsta.android.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.newsta.android.utils.models.Story
import kotlinx.coroutines.flow.Flow

@Dao
interface StoriesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<Story>)

    @Query("SELECT * FROM ${Story.TABLE_NAME}")
    suspend fun getAllStories(): List<Story>

    @Query("DELETE FROM ${Story.TABLE_NAME}")
    suspend fun deleteAllStories()

}
