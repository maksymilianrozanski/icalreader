package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.APIService
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.CalendarResponse
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.model.storage.EventDao
import io.reactivex.Observable
import javax.inject.Inject

class ModelImpl @Inject constructor(
    val apiService: APIService,
    val iCalReader: ICalReader,
    val dataSource: EventDao
) : Model {

    override fun requestNewData(): Observable<CalendarResponse<MutableList<CalendarEvent>>> {
        return Observable.concatArray(
            Observable.just(CalendarResponse.loading(mutableListOf())),
            requestCalendarResponseFromApi().doOnNext {
                if (it.status == "Success") {
                    replaceSavedEvents(it.data)
                }
            }).onErrorReturnItem(CalendarResponse.error(mutableListOf(), "Other exception"))
    }

    override fun requestSavedData(): Observable<CalendarResponse<MutableList<CalendarEvent>>> {
        return loadEventsFromDatabase()
    }

    fun requestCalendarResponseFromApi(): Observable<CalendarResponse<MutableList<CalendarEvent>>> {
        return apiService.getResponse().map {
            if (it.code() == 200 && it.body() != null) {
                val iCalString = it.body()!!.string()
                val events = iCalReader.getCalendarEvents(iCalString).toMutableList()
                events.sortBy(CalendarEvent::dateStart)
                val successResponse = CalendarResponse.success(events)
                successResponse
            } else {
                CalendarResponse.error(mutableListOf(), it.code().toString())
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
        dataSource.deleteAllCalendars()
        val calendar =
            WebCalendar(calendarName = "Temp name", calendarUrl = "http://10.0.2.2:8080/api/test.ical")
        dataSource.insertCalendar(calendar)

        val eventsCorrectIds = mutableListOf<CalendarEvent>()

        events.forEach {
            val event = CalendarEvent(
                calendarId = calendar.calendarId,
                title = it.title,
                dateStart = it.dateStart,
                dateEnd = it.dateEnd,
                description = it.description,
                location = it.location
            )
            eventsCorrectIds.add(event)
        }

        dataSource.deleteAllEvents()
        dataSource.insertEventsList(eventsCorrectIds)
    }
}