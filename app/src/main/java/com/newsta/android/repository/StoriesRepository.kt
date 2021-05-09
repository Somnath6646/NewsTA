package com.newsta.android.repository

import android.provider.ContactsContract
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.newsta.android.NewstaApp
import com.newsta.android.data.local.StoriesDAO
import com.newsta.android.remote.data.*
import com.newsta.android.remote.services.NewsService
import com.newsta.android.responses.LogoutResponse
import com.newsta.android.responses.NewsSourceResponse
import com.newsta.android.responses.SearchStory
import com.newsta.android.utils.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.http.Body
import java.io.IOException
import java.lang.Exception
import java.net.ConnectException

class StoriesRepository(
    private val storiesDao: StoriesDAO,

    private val newsService: NewsService
) {


    suspend fun getStoryByIDFromSearch(searchByStoryIDRequest: SearchByStoryIDRequest): Flow<DataState<ArrayList<Story>?>> = flow{
        emit(DataState.Loading)
        println("LOADING")

        try {

            val response = newsService.getSearchedStoryById(searchByStoryIDRequest)

            if (!response.isSuccessful) {
                Log.i("MYTAG", response.message())
                val gson = Gson()
                val type = object : TypeToken<ErrorResponse>() {}.type
                var errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)
                if (errorResponse != null) {
                    emit(DataState.Error(errorResponse.detail))
                }
            }

            val searchedStories = newsService.getSearchedStoryById(searchByStoryIDRequest).body()?.data

            emit(DataState.Success(searchedStories))

        } catch (e: Exception) {

            Log.i("MYTAG", e.message.toString())
            if (e is ConnectException){
                emit(DataState.Error("No network connection"))
            }else{
                emit(DataState.Error(e.message.toString()))
            }

        }
    }

    suspend fun getSearchResults(searchRequest: SearchRequest): Flow<DataState<List<SearchStory>?>> = flow {
        emit(DataState.Loading)
        println("LOADING")

        try {

            val response = newsService.getSearchResults(searchRequest)
            val searchedStories = response.body()?.data
            if (!response.isSuccessful) {
                Log.i("MYTAG", response.message())
                val gson = Gson()
                val type = object : TypeToken<ErrorResponse>() {}.type
                var errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)
                if (errorResponse != null) {
                    emit(DataState.Error(errorResponse.detail))
                }
            }
            emit(DataState.Success(searchedStories))

        } catch (e: Exception) {

            Log.i("MYTAG", e.message.toString())
            if (e is ConnectException){
                emit(DataState.Error("No network connection"))
            }else{
                emit(DataState.Error(e.message.toString()))
            }

        }
    }

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
        } catch (e: Exception) {
            println("ERROR IN FETCHING EXTRAS")
        }

    }

    suspend fun getSources(sourceRequest: NewsSourceRequest): Flow<DataState<List<NewsSource>?>> = flow{
        emit(DataState.Loading)
        try{
          val sources =  newsService.getSource(sourceRequest)
          if(sources.isSuccessful){
             emit(DataState.Success(sources.body()?.data))
          }else {
              val gson = Gson()
              val type = object : TypeToken<ErrorResponse>() {}.type
              var errorResponse: ErrorResponse? = gson.fromJson(sources.errorBody()!!.charStream(), type)
              if (errorResponse != null) {
                  emit(DataState.Error(errorResponse.detail))
              }
          }
        }catch (e: Exception){
            if (e is ConnectException){
                emit(DataState.Error("To see sources connect to internet"))
            }
        }
    }

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

    suspend fun deleteAllStories() {

        storiesDao.deleteAllStories()
        NewstaApp.is_database_empty = true
        NewstaApp.setIsDatabaseEmpty(true)

    }

    suspend fun getCategories(categoryRequest: CategoryRequest): Flow<DataState<List<Category>?>> = flow{
        emit(DataState.Loading)
        try {
            val response = newsService.getCategories(categoryRequest)
            if (response.isSuccessful){
                emit(DataState.Success(response.body()?.data))
                response.body()?.data?.let { storiesDao.insertCategories(it) }


            }else{
                val gson = Gson()
                val type = object : TypeToken<ErrorResponse>() {}.type
                var errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)
                if (errorResponse != null) {
                    emit(DataState.Error(errorResponse.detail))
                }
            }
        }catch (e: Exception){
            val categories = storiesDao.getAllCategories()
            emit(DataState.Success(categories))

            if (e is ConnectException){
                emit(DataState.Error("Enjoy Offline Mode :)"))
            }else{
                emit(DataState.Error(e.message.toString()))
            }
        }
    }

    suspend fun logout(logoutRequest: LogoutRequest): Flow<DataState<LogoutResponse?>> = flow{
        emit(DataState.Loading)
        try{
            val response = newsService.logout(logoutRequest)
            if (response.isSuccessful){

                emit(DataState.Success(response.body()))
            }else{
                val gson = Gson()
                val type = object : TypeToken<ErrorResponse>() {}.type
                var errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)
                if (errorResponse != null) {
                    emit(DataState.Error(errorResponse.detail))
                }
            }

        }catch (e: Exception){

            if (e is ConnectException){
                DataState.Error("No network connection")
            }else{
                DataState.Error(e.message.toString())
            }

        }
    }


}
