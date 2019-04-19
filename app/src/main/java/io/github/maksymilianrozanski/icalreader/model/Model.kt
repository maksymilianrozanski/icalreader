package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.CalendarData
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.reactivex.Observable

interface Model {

    fun requestNewData(): Observable<ResponseWrapper<MutableList<CalendarEvent>>>

    fun requestCalendarData(webCalendar: WebCalendar): Observable<ResponseWrapper<CalendarData>>

    fun requestSavedData(): Observable<ResponseWrapper<MutableList<CalendarEvent>>>

    fun requestSavedData(webCalendar: WebCalendar): Observable<ResponseWrapper<CalendarData>>

    fun saveNewCalendar(calendarName: String, url: String)

    fun requestSavedCalendars(): Observable<List<WebCalendar>>

    fun deleteCalendar(calendar: WebCalendar)
}