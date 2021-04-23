package com.newsta.android.utils.models


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("category")
    val category: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("event_id")
    val eventId: Int,
    @SerializedName("img_url")
    val imgUrl: String,
    @SerializedName("num_articles")
    val numArticles: Int,
    @SerializedName("story_id")
    val storyId: Int,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updated_at")
    val updatedAt: String
)