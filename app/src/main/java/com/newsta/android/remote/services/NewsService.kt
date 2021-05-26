package com.newsta.android.remote.services

import com.newsta.android.remote.data.*
import com.newsta.android.responses.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NewsService {

    @Headers("Accept: application/json")
    @POST("stories/new")
    suspend fun getAllNews(@Body newsRequest: NewsRequest): NewsResponse

    @Headers("Accept: application/json")
    @POST("stories/existing")
    suspend fun getExistingNews(@Body newsRequest: NewsRequest): NewsResponse

    @Headers("Accept: application/json")
    @POST("sources")
    suspend fun getSource(@Body sourceRequest: NewsSourceRequest): Response<NewsSourceResponse>

    @Headers("Accept: application/json")
    @POST("/categories")
    suspend fun getCategories(@Body categoryRequest: CategoryRequest): Response<CategoryResponse>

    //Search
    @Headers("Accept: application/json")
    @POST("/stories/id")
    suspend fun getSearchedStoryById(@Body searchbystoryidrequest: SearchByStoryIDRequest): Response<NewsResponse>

    @Headers("Accept: application/json")
    @POST("/search")
    suspend fun getSearchResults(@Body searchRequest: SearchRequest): Response<SearchResponse>


    @Headers("Accept: application/json")
    @POST("/logout")
    suspend fun logout(@Body logoutRequest: LogoutRequest): Response<LogoutResponse>

}
