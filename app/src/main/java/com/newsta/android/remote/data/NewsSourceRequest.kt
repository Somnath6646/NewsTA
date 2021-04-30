package com.newsta.android.remote.data

data class NewsSourceRequest(
    val access_token: String,
    val iss: String,
    val story_id: Int,
    val event_id: Int
)