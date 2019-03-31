package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.CalendarEvent

interface ICalReader {
    fun getCalendarEvents(inputICal: String): List<CalendarEvent>
}