package com.newsta.android.responses

import com.google.gson.annotations.SerializedName
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story

data class StoriesByIdsResponse(
    @SerializedName("data")
    val `data`: ArrayList<SavedStory>,
    @SerializedName("status_code")
    val statusCode: Int
)