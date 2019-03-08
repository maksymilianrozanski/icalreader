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
        val event1 = CalendarEvent(
            "title of first calendar event",
            "2019-03-03 17:00",
            "2019-03-03 19:00",
            "Description of first event",
            "Warsaw"
        )
        val event2 = CalendarEvent(
            "title of second calendar event",
            "2019-02-02 11:00",
            "2019-02-02 14:00",
            "Description of second event",
            "Europe"
        )
        return Observable.just(listOf(event1, event2))
    }
}