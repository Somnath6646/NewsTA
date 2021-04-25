package com.newsta.android.utils.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Event(
    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    val createdAt: Long = 0L,
    @ColumnInfo(name = "event_id")
    @SerializedName("event_id")
    @PrimaryKey(autoGenerate = false)
    val eventId: Int = 0,
    @ColumnInfo(name = "img_url")
    @SerializedName("img_url")
    val imgUrl: String = "",
    @ColumnInfo(name = "num_articles")
    @SerializedName("num_articles")
    val numArticles: Int = 0,
    @ColumnInfo(name = "summary")
    @SerializedName("summary")
    val summary: String = "",
    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String = "",
    @ColumnInfo(name = "updated_at")
    @SerializedName("updated_at")
    val updatedAt: Long = 0L
)