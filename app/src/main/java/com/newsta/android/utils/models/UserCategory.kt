package com.newsta.android.utils.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class UserCategory(
    @SerializedName("category")
    val category: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var isEnabled: Boolean = true
) {

    override fun equals(other: Any?): Boolean {
        val oth = other as UserCategory
        return (oth.categoryId == this.categoryId)
    }
}
