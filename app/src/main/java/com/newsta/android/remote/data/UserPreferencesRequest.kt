package com.newsta.android.remote.data


import com.google.gson.annotations.SerializedName

data class UserPreferencesRequest(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("iss")
    val iss: String
)