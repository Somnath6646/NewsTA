package com.newsta.android.utils.models


import com.google.gson.annotations.SerializedName

data class StoriesItem(
    @SerializedName("cluster")
    val cluster: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("img_url")
    val imgUrl: String,
    @SerializedName(" num_articles")
    val numArticles: Int,
    @SerializedName("sub_cluster")
    val subCluster: Int,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updated_at")
    val updatedAt: String
)