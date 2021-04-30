package com.newsta.android.remote.services

import com.newsta.android.remote.data.NewsRequest
import com.newsta.android.remote.data.NewsSourceRequest
import com.newsta.android.responses.NewsResponse
import com.newsta.android.responses.NewsSourceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NewsService {

    @Headers("Accept: application/json")
    @POST("stories/new")
    suspend fun getAllNews(@Body newsRequest: NewsRequest): NewsResponse

    @Headers("Accept: application/json")
    @POST("sources")
    suspend fun getSource(@Body sourceRequest: NewsSourceRequest): NewsSourceResponse

}