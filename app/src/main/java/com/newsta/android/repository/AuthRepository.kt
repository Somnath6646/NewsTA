  package com.newsta.android.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.newsta.android.data.local.StoriesDAO
import com.newsta.android.remote.data.*
import com.newsta.android.remote.services.AuthenticationService
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body
import java.lang.Exception
import java.net.ConnectException

class AuthRepository (
    private val api: AuthenticationService,
    private val storiesDao: StoriesDAO
) : BaseRepository() {

    suspend fun signup(
        signupRequest: SignupRequest
    ) = safeApiCall {
        api.signup(signupRequest)
    }

    suspend fun signup(
        signupRequestSI: SignUpRequest_Social
    ) = safeApiCall {
        api.signup(signupRequestSI)
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

    suspend fun skipAuth(
        skipAuthRequest: SkipAuthRequest
    ) = safeApiCall {
        api.skipAuth(skipAuthRequest)
    }

    suspend fun getUserPreferences(@Body userPreferencesRequest: UserPreferencesRequest): Flow<DataState<UserPreferences?>> =
        flow {

            try {

                val response = api.userPreferences(userPreferencesRequest)
                val prefresponse = response.body()?.data
                if (!response.isSuccessful) {
                    Log.i("MYTAG", response.message())
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    var errorResponse: ErrorResponse? =
                        gson.fromJson(response.errorBody()!!.charStream(), type)
                    if(response.code() == 499) {
                        println("SAVING USER PREFERENCES IN DB EMPTY")
                        if (errorResponse != null) {
                            emit(DataState.Error(errorResponse.detail, 499))
                        }
                        val userPreferences = UserPreferences()
//                userCategories.forEach { userCategory -> userCategory.primaryKey = 0 }
                        val isInserted = storiesDao.insertUserPreferences(userPreferences)
                        println("IS INSERTED ---> $isInserted")
                        if (isInserted != -1L) {
                            println("PREFERENCES SAVED")
                            emit(DataState.Success(userPreferences))
                        } else {
                            emit(DataState.Error("Cannot save user categories", 101))
                        }
                    }
                    if (errorResponse != null) {
                        emit(DataState.Error(errorResponse.detail))
                    }
                } else {
//                    emit(DataState.Success(prefresponse))
                    val userPreferences = prefresponse!!
//                userCategories.forEach { userCategory -> userCategory.primaryKey = 0 }
                        val isInserted = storiesDao.insertUserPreferences(userPreferences)
                        println("IS INSERTED ---> $isInserted")
                        if (isInserted != -1L) {
                            println("PREFERENCES SAVED")
                            emit(DataState.Success(userPreferences))
                        } else {
                            emit(DataState.Error("Cannot save user categories", 101))
                        }

                }
            } catch (e: Exception) {

                Log.i("MYTAG", e.message.toString())
                if (e is ConnectException) {
                    emit(DataState.Error("No network connection"))
                } else {
                    emit(DataState.Error(e.message.toString()))
                }

            }

        }
}
