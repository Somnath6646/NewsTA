package com.newsta.android.utils.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.newsta.android.utils.models.Category.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Category(
    @SerializedName("category")
    val category: String,

    @PrimaryKey(autoGenerate = false)
    @SerializedName("category_id")
    val categoryId: Int
){
    override fun equals(other: Any?): Boolean {
        return (other as Category).categoryId == this.categoryId
    }
    companion object{
        const val TABLE_NAME = "Category"
    }
}