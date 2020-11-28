package edu.syr.smalltalk.service.model.converter

import androidx.room.TypeConverter
import com.google.gson.Gson

class IntArrayConverter {
    @TypeConverter
    fun fromString(value: String): List<Int> {
        return Gson().fromJson(value, Array<Int>::class.java).asList()
    }

    @TypeConverter
    fun toString(value: List<Int>): String {
        return value.toString()
    }
}