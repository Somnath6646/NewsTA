package com.newsta.android.repository

import com.newsta.android.remote.data.NewsRequest
import com.newsta.android.remote.services.NewsService
import com.newsta.android.utils.models.Data

class NewsRepository(private val api: NewsService) : BaseRepository() {

    suspend fun getAllNews(newsRequest: NewsRequest) = safeApiCall { api.getAllNews(newsRequest) }

    /*suspend fun insertNewsToDatabase(data: ArrayList<Data>) = dao.insertStories(data)

    val stories = dao.getAllStories()*/

}
