package com.newsta.android.responses

import com.google.gson.annotations.SerializedName
import com.newsta.android.utils.models2.Data

data class NewsResponse(
        @SerializedName("data")
        val `data`: ArrayList<Data>,
        @SerializedName("status_code")
        val statusCode: Int
)