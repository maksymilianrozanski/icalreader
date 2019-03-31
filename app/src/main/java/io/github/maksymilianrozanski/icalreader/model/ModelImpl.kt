package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.APIService
import io.reactivex.Observable
import javax.inject.Inject

class ModelImpl @Inject constructor(val apiService: APIService, val iCalReader: ICalReader) : Model {

    override fun requestData(): Observable<String> {
        Thread.sleep(1000)
        return Observable.just("some text" + System.currentTimeMillis())
    }

    override fun requestEvents(): Observable<List<CalendarEvent>> {
        return apiService.getResponse().map { t ->
            println(t.code())

            val iCalString = t.body()!!.string()
            val events = iCalReader.getCalendarEvents(iCalString)

            events
        }
    }
}