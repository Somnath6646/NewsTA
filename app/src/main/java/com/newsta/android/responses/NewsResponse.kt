package com.newsta.android.responses

import com.newsta.android.utils.models.Data
import com.google.gson.annotations.SerializedName

data class NewsResponse(
        @SerializedName("data")
        val `data`: ArrayList<Data>,
        @SerializedName("status_code")
        val statusCode: Int
)