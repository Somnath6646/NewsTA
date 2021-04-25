package com.newsta.android.utils.models2


import com.google.gson.annotations.SerializedName
import kotlin.collections.List

data class NewsResponse(
    @SerializedName("data")
    val `data`: ArrayList<Data>,
    @SerializedName("status_code")
    val statusCode: Int
)