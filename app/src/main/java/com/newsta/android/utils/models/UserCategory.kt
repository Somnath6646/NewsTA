package com.newsta.android.utils.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.newsta.android.utils.models.UserCategory.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class UserCategory(
    @SerializedName("category")
    val category: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var isEnabled: Boolean = true
) {
    companion object {
        const val TABLE_NAME = "user_category"
    }

    override fun equals(other: Any?): Boolean {
        val oth = other as UserCategory
        return (oth.categoryId == this.categoryId)
    }
}
