package com.newsta.android.repository

import com.newsta.android.remote.data.NewsRequest
import com.newsta.android.remote.services.NewsService

class NewsRepository(private val api: NewsService) : BaseRepository() {

    suspend fun getAllNews(newsRequest: NewsRequest) = safeApiCall { api.getAllNews(newsRequest) }

}