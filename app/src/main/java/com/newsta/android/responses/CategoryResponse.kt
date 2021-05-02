package com.newsta.android.responses


import com.google.gson.annotations.SerializedName
import com.newsta.android.utils.models.Category

data class CategoryResponse(
    @SerializedName("data")
    val `data`: List<Category>,
    @SerializedName("status_code")
    val statusCode: Int
)