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
    val url: String,
    @SerializedName("url_icon")
    val urlIcon: String,
    @SerializedName("news_portal")
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        val source = other as NewsSource
        return source.urlIcon == this.urlIcon
    }
}