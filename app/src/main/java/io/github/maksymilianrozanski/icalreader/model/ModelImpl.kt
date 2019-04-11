package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.APIService
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.CalendarResponse
import io.github.maksymilianrozanski.icalreader.model.storage.EventDao
import io.reactivex.Observable
import javax.inject.Inject

class ModelImpl @Inject constructor(
    val apiService: APIService,
    val iCalReader: ICalReader,
    val dataSource: EventDao
) : Model {

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
                throw RequestFailedException("Response code: ${it.code()}")
            }
        }
    }

    override fun requestCalendarResponse(): Observable<CalendarResponse<MutableList<CalendarEvent>>> {
        return apiService.getResponse().map {
            if (it.code() == 200 && it.body() != null) {
                val iCalString = it.body()!!.string()
                val events = iCalReader.getCalendarEvents(iCalString)
                val successResponse = CalendarResponse.success(events) as CalendarResponse<MutableList<CalendarEvent>>
                successResponse
            } else {
                val errorResponse =
                    CalendarResponse.error(null, it.code().toString()) as CalendarResponse<MutableList<CalendarEvent>>
                errorResponse
            }
        }
    }

    private fun loadEventsFromDatabase(): List<CalendarEvent> {
        return dataSource.getAllEvents()
    }

    private fun saveEventsToDatabase(events: List<CalendarEvent>) {
        events.forEach { dataSource.insertEvent(it) }
    }
}

class RequestFailedException(message: String) : Exception(message)