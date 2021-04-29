package com.newsta.android.utils.models


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
    @SerializedName("created_at")
    val createdAt: Long = 0L,
    @SerializedName("event_id")
    val eventId: Int = 0,
    @SerializedName("img_url")
    val imgUrl: String? = "",
    @SerializedName("num_articles")
    val numArticles: Int? = 0,
    @SerializedName("summary")
    val summary: String? = "",
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("updated_at")
    val updatedAt: Long = 0L
) : Parcelable