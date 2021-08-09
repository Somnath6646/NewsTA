package com.newsta.android.remote.data


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Payload(
    @SerializedName("read")
    val read: Int,
    @SerializedName("story_id")
    val storyId: Int
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return (other as Payload).storyId == this.storyId
    }
}


class ArticleState{
    companion object{
        val READ = 0
        val UNREAD = 1
    }
}