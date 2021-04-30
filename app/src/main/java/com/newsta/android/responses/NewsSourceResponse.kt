package com.newsta.android.responses


import com.google.gson.annotations.SerializedName
import com.newsta.android.utils.models.NewsSource

data class NewsSourceResponse(
    @SerializedName("data")
    val `data`: List<NewsSource>,
    @SerializedName("status_code")
    val statusCode: Int
)