package com.newsta.android.utils.models2


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("category")
    val category: Int,
    @SerializedName("list")
    val list: String,
    @SerializedName("story_id")
    val storyId: Int
)