package com.newsta.android.utils.models


import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("category")
    val category: String,
    @SerializedName("category_id")
    val categoryId: Int
)