package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.CalendarResponse
import io.reactivex.Observable

interface Model {

    fun requestCalendarResponse(): Observable<CalendarResponse<MutableList<CalendarEvent>>>
}