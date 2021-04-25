package com.newsta.android.utils.models2

import com.google.gson.annotations.SerializedName

data class NewsItem(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("event_id")
    val eventId: Int,
    @SerializedName("img_url")
    val imgUrl: String,
    @SerializedName("num_articles")
    val numArticles: Int,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updated_at")
    val updatedAt: String
)