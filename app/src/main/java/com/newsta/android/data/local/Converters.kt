package com.newsta.android.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.newsta.android.utils.models.Event

class Converters {

    @TypeConverter
    fun listToJson(events: List<Event>) = Gson().toJson(events)

    @TypeConverter
    fun jsonToList(json: String) = Gson().fromJson(json, Array<Event>::class.java).toList()

    @TypeConverter
    fun listToJsonInt(list: List<Int>) = Gson().toJson(list)

    @TypeConverter
    fun jsonToListInt(json: String) = Gson().fromJson(json, Array<Int>::class.java).toList()

}
