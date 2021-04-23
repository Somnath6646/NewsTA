package com.newsta.android.utils.models

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("data")
    val `data`: ArrayList<Data>,
    @SerializedName("status_code")
    val statusCode: Int
)