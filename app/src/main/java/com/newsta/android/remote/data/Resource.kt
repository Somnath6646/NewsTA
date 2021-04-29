package com.newsta.android.remote.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody

sealed class Resource<out T>{

    data class Success<out T>(val data: T): Resource<T>()

    data class Failure(
            val isNetworkError: Boolean,
            val errorCode: Int?,
            val errorBody: Throwable?
    ): Resource<Nothing>()

    data class Failed(
            val message: String
    )
}

data class ErrorBody(

    @SerializedName("detail")
    @Expose
    val detail:String

)