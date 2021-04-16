package com.newsta.android.remote.services

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthenticationService {

    @FormUrlEncoded
    @POST("signup/email")
    suspend fun signup(
        @Field("email") email:String,
        @Field("password") password:String
    ): LoginResponse

}