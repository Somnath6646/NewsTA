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
import com.newsta.android.responses.SearchStory
import com.newsta.android.utils.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body
import java.lang.Exception
import java.net.ConnectException

class StoriesRepository(
    private val storiesDao: StoriesDAO,
    private val newsService: NewsService
) {

    suspend fun getStoryByIDFromSearch(searchByStoryIDRequest: SearchByStoryIDRequest): Flow<DataState<ArrayList<Story>?>> =
        flow {
            emit(DataState.Loading)
            println("LOADING")

            try {
                val response = newsService.getSearchedStoryById(searchByStoryIDRequest)

                if (!response.isSuccessful) {
                    Log.i("MYTAG", response.message())
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    var errorResponse: ErrorResponse? =
                        gson.fromJson(response.errorBody()!!.charStream(), type)
                    if (errorResponse != null) {
                        emit(DataState.Error(errorResponse.detail))
                    }
                }

                val searchedStories =
                    newsService.getSearchedStoryById(searchByStoryIDRequest).body()?.data

                emit(DataState.Success(searchedStories))

            } catch (e: Exception) {

                Log.i("MYTAG", e.message.toString())
                if (e is ConnectException) {
                    emit(DataState.Error("No network connection"))
                } else {
                    emit(DataState.Error(e.message.toString()))
                }

            }
        }

    suspend fun getSearchResults(searchRequest: SearchRequest): Flow<DataState<List<SearchStory>?>> =
        flow {
            emit(DataState.Loading)
            println("LOADING")

            try {

                val response = newsService.getSearchResults(searchRequest)
                val searchedStories = response.body()?.data
                if (!response.isSuccessful) {
                    Log.i("MYTAG", response.message())
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    var errorResponse: ErrorResponse? =
                        gson.fromJson(response.errorBody()!!.charStream(), type)
                    if (errorResponse != null) {
                        emit(DataState.Error(errorResponse.detail))
                    }
                }
                emit(DataState.Success(searchedStories))

            } catch (e: Exception) {

                Log.i("MYTAG", e.message.toString())
                if (e is ConnectException) {
                    emit(DataState.Error("No network connection"))
                } else {
                    emit(DataState.Error(e.message.toString()))
                }

            }
        }

    suspend fun getNewsFromDatabase(): Flow<DataState<List<Story>>> = flow {

        emit(DataState.Loading)
        println("LOADING")

        try {
            val cachedStories = storiesDao.getAllStories()
            println("CACHED STORIES ---> $cachedStories")
            emit(DataState.Success(cachedStories))
            println("EMITTED CACHED STORIES")
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

    suspend fun getCategoryFromDatabase(): Flow<DataState<List<Category>>> = flow {

        emit(DataState.Loading)
        println("LOADING")

        try {
            val cachedCategories = storiesDao.getAllCategories()
            println("CACHED CATEGORIES ---> $cachedCategories")
            emit(DataState.Success(cachedCategories))
            println("EMITTED CACHED CATEGORIES")

        } catch (e: Exception) {

            val cachedCategories = storiesDao.getAllCategories()
            if (cachedCategories.size == 0) {
                if (cachedCategories.isEmpty())
                else
                    emit(DataState.Success(cachedCategories))
            }

        }
    }

    suspend fun getAllStories(
        @Body newsRequest: NewsRequest,
        isRefresh: Boolean = false
    ): Flow<DataState<List<Story>>> = flow {
        emit(DataState.Loading)
        var isInCatch = false
        try {
            val remoteNewsResponse = newsService.getAllNews(newsRequest)
            val stories = remoteNewsResponse.data
            stories.sortedByDescending { story: Story -> story.updatedAt }
            emit(DataState.Success(stories))
            val isInserted = storiesDao.insertStories(stories as List<Story>)
            println("INSERTED LONG: $isInserted")
            println("INSERTED LONG: ${isInserted[0]}")
            if (isInserted[0] > 0) {
                val maxStory = storiesDao.getMaxStory()
                val minStory = storiesDao.getMinStory()
                emit(DataState.Extra(listOf(maxStory, minStory)))
            }
            if (remoteNewsResponse.statusCode == 200)
                println("SUCCESSFUL RESPONSE")
            else
                println(" ERROR: ${remoteNewsResponse.statusCode}")
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.Error("${ e.localizedMessage}"))
            isInCatch = true
        } finally {
            if(isInCatch) {
                val cachedStories = storiesDao.getAllStories()
                if (cachedStories != null) {
                    println("PRINTING FROM CATCH GET ALL NEWS ${cachedStories}")
                    if (cachedStories.size <= 0) emit(DataState.Error("Cached stories is empty"))
                    else {
                        if (!isRefresh) {
                            println("EMITTING FROM CATCH GET ALL NEWS")
                            emit(DataState.Success(cachedStories))
                        } else {
                            emit(DataState.Error("Error in news response"))
                        }
                    }
                }
            }
        }
    }

    suspend fun updateExistingStories(@Body newsRequest: NewsRequest): Flow<DataState<List<Story>>> =
        flow {

            emit(DataState.Loading)

            var isInCatch = false

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
                e.printStackTrace()
                isInCatch = true
            } finally {
                if(isInCatch) {
                    emit(DataState.Error("Error in refreshing new stories"))
                }
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

    suspend fun getSources(sourceRequest: NewsSourceRequest): Flow<DataState<List<NewsSource>?>> =
        flow {
            emit(DataState.Loading)
            var isInCatch = false
            try {
                val sources = newsService.getSource(sourceRequest)
                if (sources.isSuccessful) {
                    emit(DataState.Success(sources.body()?.data))
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    var errorResponse: ErrorResponse? =
                        gson.fromJson(sources.errorBody()!!.charStream(), type)
                    if (errorResponse != null) {
                        emit(DataState.Error(errorResponse.detail))
                    }
                }
            } catch (e: Exception) {
                if (e is ConnectException) {
                    isInCatch = true
                }
            } finally {
                if(isInCatch) {
                    emit(DataState.Error("To see sources connect to internet"))

                }
            }
        }


    suspend fun getStoriesByIds(savedStoryIds: ArrayList<Int>): Flow<DataState<ArrayList<SavedStory>>> = flow{
        emit(DataState.Loading)
        try{
            val request = StoriesByIdsRequest(accessToken = NewstaApp.access_token!!, iss = NewstaApp.ISSUER_NEWSTA, storyIds = savedStoryIds)
            val response = newsService.savedStoryByIds(request)
            if(response.isSuccessful){
                val responseBody = response.body()
                if(responseBody!= null ){
                val savedStories = responseBody.data
                    storiesDao.insertSavedStory(savedStories)
                    Log.i("Stories by ids", "saved stories $savedStoryIds")
                    emit(DataState.Success(savedStories))
                }else{
                    Log.i("Stories by ids", "response body is null")
                }
            }else{
                Log.i("Stories by ids", "response unsuccessfull")
            }
        }catch (e: Exception){
            emit(DataState.Error("Error in getting stories by ids"))
        }
    }

    suspend fun saveStoryInDB(story: SavedStory): Flow<DataState<SavedStory>> = flow {

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

    suspend fun getSavedStoriesFromDB(): Flow<DataState<List<SavedStory>>> = flow {

        emit(DataState.Loading)

        try {

            val savedStories = storiesDao.getSavedStories()
            emit(DataState.Success(savedStories))

        } catch (e: Exception) {
            emit(DataState.Error("Error in fetching saved stories"))
        }

    }

    suspend fun deleteSavedStoryInDB(story: SavedStory): Flow<DataState<SavedStory>> = flow {

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

    suspend fun deleteSavedStoryInDB(story: List<SavedStory>): Flow<DataState<SavedStory>> = flow {

        emit(DataState.Loading)

        try {

            val isDeleted = storiesDao.deleteSavedStories(story)
            if (isDeleted != -1)
                emit(DataState.Success(story[0]))
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

    suspend fun deleteAllStories(maxTime: Long) {

        storiesDao.deleteAllStories(maxTime)
        NewstaApp.is_database_empty = true
        NewstaApp.setIsDatabaseEmpty(true)

    }

    suspend fun getCategories(categoryRequest: CategoryRequest): Flow<DataState<List<Category>?>> =
        flow {
            emit(DataState.Loading)
            var isInCatch = false
            try {
                val response = newsService.getCategories(categoryRequest)
                if (response.isSuccessful) {
                    println("CATEGORIES REPO: ${response.body()?.data}")
                    emit(DataState.Success(response.body()?.data))
                    response.body()?.data?.let {
                        storiesDao.insertCategories(it)

                            val userCategoriesDB = storiesDao.getUserPreferences()
                            val userCategoriesID = arrayListOf<Int>()
                            if(userCategoriesDB.size <= 0) {
                                val userCategories = ArrayList<UserCategory>()
                                it.forEach { category ->
                                    userCategories.add(
                                        UserCategory(
                                            category.category,
                                            category.categoryId
                                        )
                                    )
                                    userCategoriesID.add(category.categoryId)
                                }
                                println("USER CATEGORIES --> $userCategories")
                                val userPreferences = UserPreferences(
                                    categories = userCategoriesID
                                )
                                storiesDao.updateUserCategories(userCategoriesID)
                            }

                    }
                } else {
                    val categories = storiesDao.getAllCategories()
                    emit(DataState.Success(categories))
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    var errorResponse: ErrorResponse? =
                        gson.fromJson(response.errorBody()!!.charStream(), type)
                    if (errorResponse != null) {
                        if(response.code() == 401) {
                            emit(DataState.Error(errorResponse.detail, response.code()))
                        } else {
                            println("ERROR RESPONSE $errorResponse")
                            emit(DataState.Error(errorResponse.detail))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isInCatch = true
            } finally {
                println("FINALLY MIEN AA GAYA")
                if(isInCatch) {
                    val categories = storiesDao.getAllCategories()
                    println("CATEGORIES REPO: $categories")
                    emit(DataState.Success(categories))
                }
            }
        }

    suspend fun logout(logoutRequest: LogoutRequest): Flow<DataState<LogoutResponse?>> = flow {
        emit(DataState.Loading)
        var isInCatch = false
        var isConnectException = false
        var message = ""
        try {
            val response = newsService.logout(logoutRequest)
            if (response.isSuccessful) {

                emit(DataState.Success(response.body()))
            } else {
                val gson = Gson()
                val type = object : TypeToken<ErrorResponse>() {}.type
                var errorResponse: ErrorResponse? =
                    gson.fromJson(response.errorBody()!!.charStream(), type)
                if (errorResponse != null) {
                    emit(DataState.Error(errorResponse.detail))
                }
            }

        } catch (e: Exception) {

            isInCatch = true
            if (e is ConnectException) {
                isConnectException = true
            } else {
                message = e.message.toString()
            }

        } finally {
            if(isInCatch) {
                if (isConnectException) {
                    emit(DataState.Error("No network connection"))
                } else {
                    emit(DataState.Error(" $message "))
                }
            }
        }
    }

    fun getSavedStory(storyId: Int): Flow<DataState<SavedStory>> = flow {

        emit(DataState.Loading)

        try {

            val savedStory = storiesDao.getSavedStory(storyId)
            println("CHECK SAVED STORY: $savedStory")
            emit(DataState.Success(savedStory))

        } catch (e: Exception) {
            emit(DataState.Error("Error in checking saved story"))
        }

    }

    suspend fun getUserPrefrences(): Flow<DataState<UserPreferences>> = flow {

        emit(DataState.Loading)
        println("LOADING")

        try {
            val cachedPreferences = storiesDao.getUserPreferences()
            println("CACHED CATEGORIES ---> $cachedPreferences")
            emit(DataState.Success(cachedPreferences[0]))
            println("EMITTED CACHED CATEGORIES")

        } catch (e: Exception) {

            val cachedPreferences = storiesDao.getUserPreferences()
            if (cachedPreferences.size == 0) {
                if (cachedPreferences.isEmpty())
                else
                    emit(DataState.Success(cachedPreferences[0]))
            }

        }
    }



    suspend fun saveUserCategoriesInServer(userCategories: ArrayList<Int>): Flow<DataState<ArrayList<Int>>> = flow {
        try {

            val updateUserCategoriesRequest = UpdateUserCategoriesRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, userCategories)
            val updateResponse = newsService.updateUserCategories(updateUserCategoriesRequest)
            if(updateResponse.isSuccessful) {
                println("RESPONSE SUCCESSFUL")
                println("SAVING AFTER SERVER ---> $userCategories")
//                userCategories.forEach { userCategory -> userCategory.primaryKey = 0 }
                    val isInserted = storiesDao.updateUserCategories(userCategories)
                    println("IS INSERTED ---> $isInserted")
                    if (isInserted != -1) {
                        println("PREFERENCES SAVED")
                        emit(DataState.Success(userCategories))
                    } else {
                        emit(DataState.Error("Cannot save user categories", 101))
                    }

            } else {
                Log.i("MYTAG", updateResponse.message())
                val gson = Gson()
                val type = object : TypeToken<ErrorResponse>() {}.type
                var errorResponse: ErrorResponse? =
                    gson.fromJson(updateResponse.errorBody()!!.charStream(), type)
                if (errorResponse != null) {
                    emit(DataState.Error(errorResponse.detail))
                }
            }

        } catch (e: Exception) {
            println("ERRORRRRRRR IN UPDATING USER CATEGORIES IN SERVER")
            e.printStackTrace()
        }
    }

    suspend fun saveSavedStoryIdsInServer(savedStoryIds: ArrayList<Int>): Flow<DataState<ArrayList<Int>>> = flow {
        try {

            val updateSavedStoryIdsRequest = UpdateSavedStoryIdsRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, savedStoryIds)
            val updateResponse = newsService.updateUserSavedStories(updateSavedStoryIdsRequest)
            if(updateResponse.isSuccessful) {
                println("RESPONSE SUCCESSFUL")
                val isInserted = storiesDao.updateSavedStoryIds(savedStoryIds)
                println("IS INSERTED ---> $isInserted")
                if (isInserted != -1) {
                    println("PREFERENCES SAVED")
                    emit(DataState.Success(savedStoryIds))
                } else {
                    emit(DataState.Error("Cannot save story id", 101))
                }

            } else {
                Log.i("MYTAG", updateResponse.message())
                val gson = Gson()
                val type = object : TypeToken<ErrorResponse>() {}.type
                var errorResponse: ErrorResponse? =
                    gson.fromJson(updateResponse.errorBody()!!.charStream(), type)
                if (errorResponse != null) {
                    emit(DataState.Error(errorResponse.detail))
                }
            }

        } catch (e: Exception) {
            println("ERRORRRRRRR IN UPDATING USER CATEGORIES IN SERVER")
            e.printStackTrace()
        }
    }


}
