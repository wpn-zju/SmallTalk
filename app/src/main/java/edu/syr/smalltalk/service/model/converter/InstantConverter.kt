package edu.syr.smalltalk.service.model.converter

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {
    @TypeConverter
    fun fromString(value: String): Instant {
        return Instant.parse(value)
    }

    @TypeConverter
    fun toString(value: Instant): String {
        return value.toString()
    }
}