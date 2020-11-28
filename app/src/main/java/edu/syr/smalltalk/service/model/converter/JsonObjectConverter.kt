package edu.syr.smalltalk.service.model.converter

import androidx.room.TypeConverter
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class JsonObjectConverter {
    @TypeConverter
    fun fromString(value: String): JsonObject {
        return JsonParser.parseString(value).asJsonObject
    }

    @TypeConverter
    fun toString(value: JsonObject): String {
        return value.toString()
    }
}