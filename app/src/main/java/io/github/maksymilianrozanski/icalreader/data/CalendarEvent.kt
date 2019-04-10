package io.github.maksymilianrozanski.icalreader.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "events")
data class CalendarEvent(
    @PrimaryKey
    @ColumnInfo(name = "eventid")
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "datestart")
    val dateStart: Date,
    @ColumnInfo(name = "dateend")
    val dateEnd: Date,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "location")
    val location: String
)