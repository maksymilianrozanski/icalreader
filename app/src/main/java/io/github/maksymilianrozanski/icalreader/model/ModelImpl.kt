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

    override fun requestCalendarResponse(): Observable<CalendarResponse<MutableList<CalendarEvent>>> {
        return Observable.concatArray(loadEventsFromDatabase(),
            requestCalendarResponseFromApi().doOnNext {
                if (it.status == "Success") {
                    replaceSavedEvents(it.data)
                }
            })
    }

    fun requestCalendarResponseFromApi(): Observable<CalendarResponse<MutableList<CalendarEvent>>> {
        return apiService.getResponse().map {
            if (it.code() == 200 && it.body() != null) {
                val iCalString = it.body()!!.string()
                val events = iCalReader.getCalendarEvents(iCalString).toMutableList()
                val successResponse = CalendarResponse.success(events)
                successResponse
            } else {
                val errorResponse =
                    CalendarResponse.error(null, it.code().toString()) as CalendarResponse<MutableList<CalendarEvent>>
                errorResponse
            }
        }
    }

    private fun loadEventsFromDatabase(): Observable<CalendarResponse<MutableList<CalendarEvent>>> {
        return dataSource.getAllEventsSingle().map { it ->
            val calendarResponse = CalendarResponse.success(it.toMutableList())
            calendarResponse
        }.toObservable()
    }

    private fun replaceSavedEvents(events: List<CalendarEvent>) {
        dataSource.deleteAllEvents()
        dataSource.insertEventsList(events)
    }
}