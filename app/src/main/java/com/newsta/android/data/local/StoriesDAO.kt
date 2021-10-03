package com.newsta.android.data.local

import androidx.room.*
import com.newsta.android.remote.data.Payload
import com.newsta.android.utils.models.*

@Dao
interface StoriesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<Story>): Array<Long>

    @Query("SELECT * FROM ${Story.TABLE_NAME}")
    suspend fun getAllStories(): List<Story>

    @Query("DELETE FROM ${Story.TABLE_NAME} WHERE updated_at <= :maxTime")
    suspend fun deleteAllStories(maxTime: Long)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendedStories(stories: List<RecommendedStory>): Array<Long>

    @Query("SELECT * FROM ${RecommendedStory.TABLE_NAME}")
    suspend fun getAllRecommendedStories(): List<RecommendedStory>

    @Query("DELETE FROM ${RecommendedStory.TABLE_NAME} WHERE updated_at <= :maxTime")
    suspend fun deleteAllRecommendedStories(maxTime: Long)


    @Query("SELECT MAX(story_id) AS storyId , MAX(updated_at) AS updatedAt FROM ${Story.TABLE_NAME} WHERE updated_at < :maxTime")
    suspend fun getMaxStory(maxTime: Long): MaxStoryAndUpdateTime


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedStory(story: SavedStory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedStory(stories: List<SavedStory>): Array<Long>


    @Query("SELECT story_id, updated_at, events, category, view_count FROM ${Story.TABLE_NAME} WHERE category = (SELECT :category FROM ${Story.TABLE_NAME})")
    suspend fun getFilteredStories(category: Int): List<Story>

    @Query("SELECT * FROM ${SavedStory.TABLE_NAME}")
    suspend fun getSavedStories(): List<SavedStory>

    @Query("SELECT story_id, updated_at, events, category, viewCount FROM ${SavedStory.TABLE_NAME} WHERE story_id = (SELECT :storyId FROM ${SavedStory.TABLE_NAME})")
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

    @Query("UPDATE user_preferences SET notify = :notifyStories WHERE `key` = 0")
    suspend fun updateNotifyStories(notifyStories: List<Payload>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = UserPreferences::class)
    suspend fun insertUserPreferences(userPreferences: UserPreferences): Long

    @Query("SELECT * FROM ${UserPreferences.TABLE_NAME}")
    suspend fun getUserPreferences(): List<UserPreferences>

    @Query("DELETE FROM ${UserPreferences.TABLE_NAME}")
    suspend fun deleteAllUserPreferences()

    @Query("DELETE FROM ${Category.TABLE_NAME}")
    suspend fun deleteAllCategories()

}
