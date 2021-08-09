package com.newsta.android.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.newsta.android.remote.data.Payload
import com.newsta.android.utils.models.Event

class Converters {

    @TypeConverter
    fun listToJson(events: List<Event>) = Gson().toJson(events)

    @TypeConverter
    fun jsonToList(json: String) = Gson().fromJson(json, Array<Event>::class.java).toList()

    @TypeConverter
    fun listToJsonInt(list: List<Int>) = Gson().toJson(list)

    @TypeConverter
    fun jsonToListInt(json2: String): List<Int>{
        println("JsonConvertor $json2")
        return Gson().fromJson(json2, Array<Int>::class.java).toList()
    }
    @TypeConverter
    fun listToJsonPayload(list: List<Payload>) : String {
        println("list $list")
        return Gson().toJson(list)
    }

    @TypeConverter
    fun jsonToListPayload(json: String): List<Payload> {
        println("Json $json")
        return Gson().fromJson(json, Array<Payload>::class.java).toList()
    }

}
