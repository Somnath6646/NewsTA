package com.newsta.android.responses

import com.newsta.android.utils.models.Story
import com.google.gson.annotations.SerializedName

data class NewsResponse(
        @SerializedName("data")
        val `data`: ArrayList<Story>,
        @SerializedName("status_code")
        val statusCode: Int
)