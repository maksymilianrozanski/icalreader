package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.CalendarData
import io.github.maksymilianrozanski.icalreader.data.CalendarForm
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.reactivex.Completable
import io.reactivex.Observable

interface Model {

    fun requestCalendarData(webCalendar: WebCalendar): Observable<ResponseWrapper<CalendarData>>

    fun requestSavedData(webCalendar: WebCalendar): Observable<ResponseWrapper<CalendarData>>

    fun requestSavedCalendars(): Observable<List<WebCalendar>>

    fun saveNewCalendar(calendarForm: CalendarForm): Observable<ResponseWrapper<CalendarForm>>

    fun deleteCalendar(calendar: WebCalendar): Completable
}