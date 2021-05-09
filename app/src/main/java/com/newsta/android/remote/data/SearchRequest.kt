package com.newsta.android.remote.data

data class SearchRequest(
    val access_token: String,
    val iss: String,
    val search_term:String
)