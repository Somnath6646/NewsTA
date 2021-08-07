package com.newsta.android.remote.data


import com.google.gson.annotations.SerializedName

data class StoriesByIdsRequest(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("iss")
    val iss: String,
    @SerializedName("story_ids")
    val storyIds: List<Int>
)