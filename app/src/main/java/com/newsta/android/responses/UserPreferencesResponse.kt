package com.newsta.android.responses


import com.google.gson.annotations.SerializedName
import com.newsta.android.utils.models.UserPreferences

data class UserPreferencesResponse(
    @SerializedName("data")
    val `data`: UserPreferences,
    @SerializedName("status_code")
    val statusCode: Int
)