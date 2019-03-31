package io.github.maksymilianrozanski.icalreader.data

data class CalendarEvent(
    val title: String,
    val dateStart: String,
    val dateEnd: String,
    val description: String,
    val location: String
)