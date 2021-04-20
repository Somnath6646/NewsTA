package com.newsta.android.remote.services

import com.newsta.android.remote.data.SignupRequest
import com.newsta.android.responses.LoginResponse
import okhttp3.RequestBody
import retrofit2.http.*

interface AuthenticationService {

    @Headers("Accept: application/json")
    @POST("signup/email")
    suspend fun signup(
        @Body signupRequest: SignupRequest
    ): LoginResponse

}