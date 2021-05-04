package com.newsta.android.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.newsta.android.utils.models.SavedStory
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

    @Query("SELECT story_id, updated_at, events, category FROM ${Story.TABLE_NAME} WHERE story_id = (SELECT MAX(story_id) FROM ${Story.TABLE_NAME})")
    suspend fun getMaxStory(): Story

    @Query("SELECT story_id, updated_at, events, category FROM ${Story.TABLE_NAME} WHERE story_id = (SELECT MIN(story_id) FROM ${Story.TABLE_NAME})")
    suspend fun getMinStory(): Story

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedStory(story: SavedStory): Long

    @Query("SELECT story_id, updated_at, events, category FROM ${Story.TABLE_NAME} WHERE category = (SELECT :category FROM ${Story.TABLE_NAME})")
    suspend fun getFilteredStories(category: Int): List<Story>

    @Query("SELECT * FROM ${SavedStory.TABLE_NAME}")
    suspend fun getSavedStories(): List<SavedStory>

    @Delete
    suspend fun deleteSavedStory(savedStory: SavedStory): Int


}
