package com.newsta.android.remote.data


import com.google.gson.annotations.SerializedName

data class UpdateNotifyStoryIdsRequest(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("iss")
    val iss: String,
    @SerializedName("payload")
    val payload: List<Payload>
)