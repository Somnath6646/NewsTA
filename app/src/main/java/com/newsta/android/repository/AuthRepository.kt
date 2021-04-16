package com.newsta.android.repository

import com.newsta.android.remote.services.AuthenticationService

class AuthRepository (
    private val api: AuthenticationService
) : BaseRepository(){

    suspend fun signup(
        email: String,
        password: String
    ) = safeApiCall {
        api.signup(email, password)
    }

}