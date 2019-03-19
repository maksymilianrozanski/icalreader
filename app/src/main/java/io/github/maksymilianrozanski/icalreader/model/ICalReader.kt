package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.CalendarEvent

interface ICalReader {
    fun getCalendarEvents(inputICal: String): List<CalendarEvent>
}