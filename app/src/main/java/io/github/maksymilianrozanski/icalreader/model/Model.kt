package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.CalendarResponse
import io.reactivex.Observable

interface Model {

    fun requestData(): Observable<String>

    fun requestEvents(): Observable<CalendarResponse<MutableList<CalendarEvent>>>
}