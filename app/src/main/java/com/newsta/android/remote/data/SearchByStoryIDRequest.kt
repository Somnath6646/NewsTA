package com.newsta.android.remote.data

data class SearchByStoryIDRequest(
    val access_token: String,
    val iss: String,
    val story_id: Int
)