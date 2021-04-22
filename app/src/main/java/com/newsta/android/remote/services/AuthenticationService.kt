package com.newsta.android.remote.services

import com.newsta.android.remote.data.SigninRequest
import com.newsta.android.remote.data.SignupRequest
import com.newsta.android.responses.SigninResponse
import com.newsta.android.responses.SignupResponse
import retrofit2.http.*

interface AuthenticationService {

    @Headers("Accept: application/json")
    @POST("signup/email")
    suspend fun signup(
        @Body signupRequest: SignupRequest
    ): SignupResponse


    @Headers("Accept: application/json")
    @POST("login/email")
    suspend fun signin(
            @Body signupRequest: SigninRequest
    ): SigninResponse

}