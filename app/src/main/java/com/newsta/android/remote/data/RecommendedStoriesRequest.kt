package com.newsta.android.remote.data


data class RecommendedStoriesRequest(
    val accessToken: String,
    val iss: String,
    val storyIDs: List<Int> = listOf()
)