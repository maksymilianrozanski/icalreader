package io.github.maksymilianrozanski.icalreader.data

import java.util.*

data class CalendarEvent(
    val title: String,
    val dateStart: Date,
    val dateEnd: Date,
    val description: String,
    val location: String
)