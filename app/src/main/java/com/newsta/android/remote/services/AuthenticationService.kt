package com.newsta.android.remote.services

import com.newsta.android.remote.data.*
import com.newsta.android.responses.SigninResponse
import com.newsta.android.responses.SignupResponse
import com.newsta.android.responses.UserPreferencesResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthenticationService {

    @Headers("Accept: application/json")
    @POST("signup/email")
    suspend fun signup(
        @Body signupRequest: SignupRequest
    ): Response<SignupResponse>


    @Headers("Accept: application/json")
    @POST("signup/si")
    suspend fun signup(
            @Body signupRequest: SignUpRequest_Social
    ): Response<SignupResponse>

    @Headers("Accept: application/json")
    @POST("login/email")
    suspend fun signin(
            @Body signupRequest: SigninRequest
    ): Response<SigninResponse>

    @Headers("Accept: application/json")
    @POST("login/si")
    suspend fun signin(
            @Body signinRequest: SignInRequest_Social
    ): Response<SigninResponse>

    @Headers("Accept: application/json")
    @POST("user_preferences")
    suspend fun userPreferences(
            @Body userPreferencesRequest: UserPreferencesRequest
    ): Response<UserPreferencesResponse>

}