package com.newsta.android.utils.models


import com.google.gson.annotations.SerializedName

data class NewsSource(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("img_url")
    val imgUrl: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String
)