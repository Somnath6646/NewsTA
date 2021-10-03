package com.newsta.android.responses

import com.google.gson.annotations.SerializedName
import com.newsta.android.utils.models.RecommendedStory
import com.newsta.android.utils.models.Story

data class RecommendedStoriesResponse(
    @SerializedName("data")
    val `data`: ArrayList<RecommendedStory>,
    @SerializedName("status_code")
    val statusCode: Int
)