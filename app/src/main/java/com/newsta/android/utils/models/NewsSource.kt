package com.newsta.android.utils.models


import android.util.Log
import com.google.gson.annotations.SerializedName

private const val TAG = "NewsSource"

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
        Log.i(TAG, "equals: ${this.name == source.name}")
        return this.name == source.name
    }
}