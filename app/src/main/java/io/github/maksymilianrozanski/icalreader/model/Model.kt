package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.CalendarResponse
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.reactivex.Observable

interface Model {

    fun requestNewData(): Observable<CalendarResponse<MutableList<CalendarEvent>>>

    fun requestSavedData(): Observable<CalendarResponse<MutableList<CalendarEvent>>>

    fun saveNewCalendar(calendarName: String, url: String)

    fun requestSavedCalendars(): Observable<List<WebCalendar>>

    fun deleteCalendar(calendar: WebCalendar)
}