package com.newsta.android.responses


import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("data")
    val `data`: String,
    @SerializedName("status_code")
    val statusCode: Int
)