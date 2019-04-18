package io.github.maksymilianrozanski.icalreader.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "calendars",
    indices = [Index(value = ["calendarid", "calendarname", "calendarurl"], unique = true)]
)

data class WebCalendar(
    @PrimaryKey
    @ColumnInfo(name = "calendarid")
    val calendarId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "calendarname")
    val calendarName: String,
    @ColumnInfo(name = "calendarurl")
    val calendarUrl: String
)