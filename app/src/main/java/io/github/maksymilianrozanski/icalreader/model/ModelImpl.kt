package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.APIService
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.reactivex.Observable
import javax.inject.Inject

class ModelImpl @Inject constructor(val apiService: APIService, val iCalReader: ICalReader) : Model {

    override fun requestData(): Observable<String> {
        Thread.sleep(1000)
        return Observable.just("some text" + System.currentTimeMillis())
    }

    override fun requestEvents(): Observable<MutableList<CalendarEvent>> {
        return apiService.getResponse().map {
            if (it.code() == 200) {
                val iCalString = it.body()!!.string()
                val events = iCalReader.getCalendarEvents(iCalString)
                events as MutableList<CalendarEvent>
            } else {
                println("Throwing exception, code: ${it.code()}")
                throw Exception("Response code: ${it.code()}")
            }
        }
    }
}