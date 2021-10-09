package com.newsta.android.utils.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.newsta.android.utils.models.RecommendedStory.Companion.TABLE_NAME
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TABLE_NAME)
data class RecommendedStory(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "category")
    @SerializedName("category")
    val category: Int,
    @ColumnInfo(name = "updated_at")
    @SerializedName("updated_at")
    val updatedAt: Long,
    @SerializedName("events")
    var events: List<Event>,
    @ColumnInfo(name = "story_id")
    @SerializedName("story_id")
    val storyId: Int,
    @ColumnInfo(name = "view_count")
    @SerializedName("view_count")
    val viewCount: Int,
    @ColumnInfo(name = "read")
    val read: Boolean = false
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        val new = other as Story
        return this.storyId == new.storyId
    }



    companion object {
        const val TABLE_NAME = "recommended_stories"
    }
}
