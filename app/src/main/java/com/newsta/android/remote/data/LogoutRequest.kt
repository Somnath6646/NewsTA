package com.newsta.android.remote.data


import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("iss")
    val iss: String
)