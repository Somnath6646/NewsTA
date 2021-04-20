package com.newsta.android.repository

import com.newsta.android.remote.data.SignupRequest
import com.newsta.android.remote.services.AuthenticationService
import okhttp3.RequestBody

class AuthRepository (
    private val api: AuthenticationService
) : BaseRepository(){

    suspend fun signup(
        signupRequest: SignupRequest
    ) = safeApiCall {
        api.signup(signupRequest)
    }

}