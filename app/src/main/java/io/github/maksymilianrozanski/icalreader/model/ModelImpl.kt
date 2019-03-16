package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.APIService
import io.reactivex.Observable
import javax.inject.Inject

class ModelImpl @Inject constructor(val apiService: APIService) : Model {

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

        return apiService.getResponse().map { t ->
            println(t.code())

            val iCalString = t.body()!!.string()
            val iCalReader = ICalReader()
            val events = iCalReader.getCalendarEvents(iCalString)

            events
        }
    }
}