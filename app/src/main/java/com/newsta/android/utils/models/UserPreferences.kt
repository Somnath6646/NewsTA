package com.newsta.android.utils.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.newsta.android.utils.models.UserPreferences.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class UserPreferences(
    @SerializedName("category")
    var categories: List<Int>? = arrayListOf(),
    @SerializedName("saved")
    val saved: List<Int>? = arrayListOf(),
    @SerializedName("notify")
    val notify: List<Int>? = arrayListOf(),
    @PrimaryKey(autoGenerate = false)
    val key: Int = 0
) {
    companion object {
        const val TABLE_NAME = "user_preferences"
    }
}
