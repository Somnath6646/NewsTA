package com.newsta.android.repository

import com.newsta.android.remote.data.SignInRequest_Social
import com.newsta.android.remote.data.SigninRequest
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

    suspend fun signin(
            signinRequest: SigninRequest
    ) = safeApiCall {
        api.signin(signinRequest)
    }

    suspend fun signin(
            signinRequestSI: SignInRequest_Social
    ) = safeApiCall {
        api.signin(signinRequestSI)
    }


}