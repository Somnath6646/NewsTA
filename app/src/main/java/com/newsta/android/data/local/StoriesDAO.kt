package com.newsta.android.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.newsta.android.utils.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StoriesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<Story>): Array<Long>

    @Query("SELECT * FROM ${Story.TABLE_NAME}")
    suspend fun getAllStories(): List<Story>

    @Query("DELETE FROM ${Story.TABLE_NAME} WHERE updated_at <= :maxTime")
    suspend fun deleteAllStories(maxTime: Long)

    @Query("SELECT story_id, updated_at, events, category FROM ${Story.TABLE_NAME} WHERE story_id = (SELECT MAX(story_id) FROM ${Story.TABLE_NAME})")
    suspend fun getMaxStory(): Story

    @Query("SELECT story_id, updated_at, events, category FROM ${Story.TABLE_NAME} WHERE story_id = (SELECT MIN(story_id) FROM ${Story.TABLE_NAME})")
    suspend fun getMinStory(): Story

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedStory(story: SavedStory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedStory(stories: List<SavedStory>): Array<Long>


    @Query("SELECT story_id, updated_at, events, category FROM ${Story.TABLE_NAME} WHERE category = (SELECT :category FROM ${Story.TABLE_NAME})")
    suspend fun getFilteredStories(category: Int): List<Story>

    @Query("SELECT * FROM ${SavedStory.TABLE_NAME}")
    suspend fun getSavedStories(): List<SavedStory>

    @Query("SELECT story_id, updated_at, events, category FROM ${SavedStory.TABLE_NAME} WHERE story_id = (SELECT :storyId FROM ${SavedStory.TABLE_NAME})")
    suspend fun getSavedStory(storyId: Int): SavedStory

    @Delete
    suspend fun deleteSavedStory(savedStory: SavedStory): Int

    @Delete
    suspend fun deleteSavedStories(savedStory: List<SavedStory>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Query("SELECT * FROM ${Category.TABLE_NAME}")
    suspend fun getAllCategories(): List<Category>

    @Query("UPDATE user_preferences SET categories = :userCategories WHERE `key` = 0")
    suspend fun updateUserCategories(userCategories: List<Int>): Int


    @Query("UPDATE user_preferences SET saved = :savedStoryIds WHERE `key` = 0")
    suspend fun updateSavedStoryIds(savedStoryIds: List<Int>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = UserPreferences::class)
    suspend fun insertUserPreferences(userPreferences: UserPreferences): Long

    @Query("SELECT * FROM ${UserPreferences.TABLE_NAME}")
    suspend fun getUserPreferences(): List<UserPreferences>

    @Query("DELETE FROM ${UserPreferences.TABLE_NAME}")
    suspend fun deleteAllUserPreferences()

    @Query("DELETE FROM ${Category.TABLE_NAME}")
    suspend fun deleteAllCategories()

}
