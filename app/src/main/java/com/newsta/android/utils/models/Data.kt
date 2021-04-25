package com.newsta.android.utils.models


import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "newsta_database")
data class Data(
    @ColumnInfo(name = "category")
    @SerializedName("category")
    val category: Int = 0,
    @Embedded(prefix = "events")
    @SerializedName("events")
    val events: List<Event> = listOf(),
    @ColumnInfo(name = "story_id")
    @PrimaryKey(autoGenerate = false)
    @SerializedName("story_id")
    val storyId: Int = 0
)