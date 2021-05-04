package com.newsta.android.repository

import com.newsta.android.data.local.StoriesDAO
import com.newsta.android.remote.data.CategoryRequest
import com.newsta.android.remote.data.NewsRequest
import com.newsta.android.remote.data.NewsSourceRequest
import com.newsta.android.remote.services.NewsService
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body
import java.io.IOException
import java.lang.Exception

class StoriesRepository(
    private val storiesDao: StoriesDAO,
    private val newsService: NewsService
) {

    suspend fun getNewsFromDatabase(): Flow<DataState<List<Story>>> = flow {

        emit(DataState.Loading)
        println("LOADING")

        try {

            val cachedStories = storiesDao.getAllStories()
            emit(DataState.Success(cachedStories))
            val maxStory = storiesDao.getMaxStory()
            emit(DataState.Extra(listOf(maxStory)))

        } catch (e: Exception) {

            val cachedStories = storiesDao.getAllStories()
            if (cachedStories.size == 0) {
                if (cachedStories.isEmpty())

                else
                    emit(DataState.Success(cachedStories))
            }

        }
    }

    suspend fun getAllStories(@Body newsRequest: NewsRequest): Flow<DataState<List<Story>>> = flow {
        emit(DataState.Loading)
        try {
            val remoteNewsResponse = newsService.getAllNews(newsRequest)
            val stories = remoteNewsResponse.data
            stories.sortedByDescending { story: Story -> story.updatedAt }
            emit(DataState.Success(stories))
            val isInserted = storiesDao.insertStories(stories as List<Story>)
            if (isInserted[0] > 0) {
                val maxStory = storiesDao.getMaxStory()
                val minStory = storiesDao.getMinStory()
                emit(DataState.Extra(listOf(maxStory, minStory)))
            }
            if(remoteNewsResponse.statusCode == 200)
                println("SUCCESSFUL RESPONSE")
            else
                println(" ERROR: ${remoteNewsResponse.statusCode}")
        } catch (e: Exception) {
            val cachedStories = storiesDao.getAllStories()
            if (cachedStories != null) {
                if (cachedStories.isEmpty()) emit(DataState.Error("Error in news response"))
                else emit(DataState.Success(cachedStories))
            }
        }
    }

    suspend fun updateExistingStories(@Body newsRequest: NewsRequest): Flow<DataState<List<Story>>> = flow {

        emit(DataState.Loading)

        try {

            val updateResponse = newsService.getExistingNews(newsRequest)
            val stories = updateResponse.data
            storiesDao.insertStories(stories)
            emit(DataState.Success(stories)).let {
                val maxStory = storiesDao.getMaxStory()
                val minStory = storiesDao.getMinStory()
                emit(DataState.Extra(listOf(maxStory, minStory)))
            }

        } catch (e: Exception) {
            emit(DataState.Error("Error in updating the stories"))
        }

    }

    suspend fun getMaxAndMinStory(): Flow<DataState<List<Story>>> = flow {

        emit(DataState.Loading)

        try {
            val maxStory = storiesDao.getMaxStory()
            val minStory = storiesDao.getMinStory()
            emit(DataState.Success(listOf(maxStory, minStory)))
        } catch (e: Exception) {}

    }

    suspend fun getSources(sourceRequest: NewsSourceRequest) = newsService.getSource(sourceRequest)

    suspend fun saveStory(story: SavedStory): Flow<DataState<SavedStory>> = flow {

        emit(DataState.Loading)

        try {
            val isSaved = storiesDao.insertSavedStory(story)
            if (isSaved > 0)
                emit(DataState.Success(story))
            else
                emit(DataState.Error("Error in saving story"))
        } catch (e: Exception) {
            emit(DataState.Error("Error in saving story"))
        }

    }

    suspend fun getSavedStories(): Flow<DataState<List<SavedStory>>> = flow {

        emit(DataState.Loading)

        try {

            val savedStories = storiesDao.getSavedStories()
            emit(DataState.Success(savedStories))

        } catch (e: Exception) {
            emit(DataState.Error("Error in fetching saved stories"))
        }

    }

    suspend fun deleteSavedStory(story: SavedStory): Flow<DataState<SavedStory>> = flow {

        emit(DataState.Loading)

        try {

            val isDeleted = storiesDao.deleteSavedStory(story)
            if (isDeleted != -1)
                emit(DataState.Success(story))
            else
                emit(DataState.Error("Error in deleting saved story"))

        } catch (e: Exception) {
            emit(DataState.Error("Error in deleting saved story"))
        }

    }

    suspend fun getFilteredStories(category: Int): Flow<DataState<List<Story>>> = flow {

        emit(DataState.Loading)

        try {

            val filteredStories = storiesDao.getFilteredStories(category)
            println("DATABASE FILTER: $filteredStories")
            emit(DataState.Success(filteredStories))
            print("EMITTED FILTERED")

        } catch (e: Exception) {
            emit(DataState.Error("Error in fetching filtered storiesS"))
        }

    }

    suspend fun getCategories(categoryRequest: CategoryRequest) = newsService.getCategories(categoryRequest)

}
