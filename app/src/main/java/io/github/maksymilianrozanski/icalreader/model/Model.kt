package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.reactivex.Observable

interface Model {

    fun requestNewData(): Observable<ResponseWrapper<MutableList<CalendarEvent>>>

    fun requestSavedData(): Observable<ResponseWrapper<MutableList<CalendarEvent>>>

    fun saveNewCalendar(calendarName: String, url: String)

    fun requestSavedCalendars(): Observable<List<WebCalendar>>

    fun deleteCalendar(calendar: WebCalendar)
}