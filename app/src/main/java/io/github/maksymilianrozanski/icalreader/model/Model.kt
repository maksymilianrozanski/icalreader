package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.CalendarEvent
import io.reactivex.Observable

interface Model {

    fun requestData(): Observable<String>

    fun requestEvents(): Observable<List<CalendarEvent>>
}