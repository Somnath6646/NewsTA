package com.newsta.android.utils.models


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    @ColumnInfo(name = "category")
    @SerializedName("category")
    val category: Int,
    @SerializedName("events")
    val events: List<Event>,
    @ColumnInfo(name = "story_id")
    @PrimaryKey(autoGenerate = false)
    @SerializedName("story_id")
    val storyId: Int
) : Parcelable