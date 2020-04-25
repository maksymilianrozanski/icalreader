package io.github.maksymilianrozanski.icalreader.model.main.storage

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun toDate(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun toLong(date: Date): Long {
        return date.time
    }
}