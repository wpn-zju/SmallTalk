package edu.syr.smalltalk.service.model.converter

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {
    @TypeConverter
    fun fromLong(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun toLong(value: Instant): Long {
        return value.toEpochMilli()
    }
}
