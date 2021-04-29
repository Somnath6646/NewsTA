package com.newsta.android.repository

import com.newsta.android.data.local.StoriesDAO
import com.newsta.android.remote.data.NewsRequest
import com.newsta.android.remote.services.NewsService
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body
import java.lang.Exception


class StoryRepository (
        private val storiesDao: StoriesDAO,
        private val newsService: NewsService
)  {


    suspend fun getAllStories(@Body newsRequest: NewsRequest): Flow<DataState<List<Story>>> = flow{
        emit(DataState.Loading)
        try {
            val remoteNewsResponse = newsService.getAllNews(newsRequest)
            val stories = remoteNewsResponse.data
            storiesDao.deleteAllStories()
            storiesDao.insertStories(stories as List<Story>)
            val cachedStories = storiesDao.getAllStories()
            if (cachedStories != null) {emit(DataState.Sucess(cachedStories))
            }
        }catch (e: Exception){
            val cachedStories = storiesDao.getAllStories()
            if (cachedStories != null) {
                if (cachedStories.isEmpty()) emit(DataState.Error(e))
                else emit(DataState.Sucess(cachedStories))
            }
        }
    }
}