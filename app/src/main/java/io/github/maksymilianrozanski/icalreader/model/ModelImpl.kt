package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.CalendarEvent
import io.reactivex.Observable

class ModelImpl : Model {

    override fun requestData(): Observable<String> {
        Thread.sleep(1000)
        return Observable.just("some text" + System.currentTimeMillis())
    }

    override fun requestEvents(): Observable<List<CalendarEvent>> {
        Thread.sleep(500)
        val event1 = CalendarEvent("name of first calendar event", "2019-03-03")
        val event2 = CalendarEvent("name of second calendar event", "2019-02-02")
        return Observable.just(listOf(event1, event2))
    }
}