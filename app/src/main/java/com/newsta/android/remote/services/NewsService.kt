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
    @POST("recommendation")
    suspend fun getRecommendedStories(@Body recommendedStoriesRequest: RecommendedStoriesRequest): RecommendedStoriesResponse

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

    @Headers("Accept: application/json")
    @POST("user_preferences/update_categories")
    suspend fun updateUserCategories(@Body updateUserCategoriesRequest: UpdateUserCategoriesRequest): Response<UpdateUserCategoriesResponse>

    @Headers("Accept: application/json")
    @POST("user_preferences/update_saved_stories")
    suspend fun updateUserSavedStories(@Body updateSavedStoryIdsRequest: UpdateSavedStoryIdsRequest): Response<SaveStoriesUpdatedResponse>

    @Headers("Accept: application/json")
    @POST("user_preferences/update_notify_stories")
    suspend fun updateUserNotifyStories(@Body updateNotifyStoryIdsRequest: UpdateNotifyStoryIdsRequest): Response<UpdateNotifyStoryIdsResponse>

    @Headers("Accept: application/json")
    @POST("stories/ids")
    suspend fun savedStoryByIds(@Body storyByIdsRequest: StoriesByIdsRequest): Response<StoriesByIdsResponse>

}
