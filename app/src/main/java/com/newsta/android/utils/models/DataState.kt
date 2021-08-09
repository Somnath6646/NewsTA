package com.newsta.android.utils.models

import java.lang.Exception

sealed class DataState<out R> {
    data class Success<out T>(val data: T): DataState<T>()
    data class Error(val exception: String, val statusCode: Int = 0): DataState<Nothing>()
    data class Extra(val data: MaxStoryAndUpdateTime): DataState<Nothing>()
    data class Unauthorised(val statusCode: Int): DataState<Nothing>()
    object Loading: DataState<Nothing>()
}