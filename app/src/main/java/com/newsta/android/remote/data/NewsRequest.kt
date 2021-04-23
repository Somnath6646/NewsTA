package com.newsta.android.remote.data

data class NewsRequest(
        val access_token: String,
        val iss: String,
        val max_story_id: Int,
        val max_datetime: String
)