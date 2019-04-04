package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.reactivex.Observable

interface Model {

    fun requestData(): Observable<String>

    fun requestEvents(): Observable<MutableList<CalendarEvent>>
}