package com.newsta.android.utils.models


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.newsta.android.remote.data.Payload
import com.newsta.android.utils.models.SavedStory.Companion.TABLE_NAME
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TABLE_NAME)
data class SavedStory(
    @ColumnInfo(name = "category")
    @SerializedName("category")
    val category: Int,
    @ColumnInfo(name = "updated_at")
    @SerializedName("updated_at")
    val updatedAt: Long,
    @SerializedName("events")
    val events: List<Event>,
    @ColumnInfo(name = "story_id")
    @PrimaryKey(autoGenerate = false)
    @SerializedName("story_id")
    val storyId: Int
) : Parcelable {
    companion object {
        const val TABLE_NAME = "newsta_saved_stories"
    }

    override fun equals(other: Any?): Boolean {
       return when(other){
            is SavedStory -> {
                this.storyId == (other).storyId
            }
           is Int -> {
               this.storyId == other
           }
           is Payload -> {
               this.storyId == (other).storyId
           }
           else -> {
               this.storyId == (other as SavedStory).storyId
           }
        }

    }
}
