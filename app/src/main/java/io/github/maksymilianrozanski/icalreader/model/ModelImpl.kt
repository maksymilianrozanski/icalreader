package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.*
import io.github.maksymilianrozanski.icalreader.model.storage.EventDao
import io.reactivex.Observable
import javax.inject.Inject

class ModelImpl @Inject constructor(
    val apiService: APIService,
    val iCalReader: ICalReader,
    val dataSource: EventDao
) : Model {

    override fun requestNewData(): Observable<ResponseWrapper<MutableList<CalendarEvent>>> {
        return Observable.concatArray(
            Observable.just(ResponseWrapper.loading(mutableListOf())),
            requestCalendarResponseFromApi().doOnNext {
                if (it.status == "Success") {
                    replaceSavedEvents(it.data)
                }
            }).onErrorReturnItem(ResponseWrapper.error(mutableListOf(), "Other exception"))
    }

    override fun requestCalendarData(webCalendar: WebCalendar): Observable<ResponseWrapper<CalendarData>> {
        return Observable.concatArray(
            Observable.just(ResponseWrapper.loading(CalendarData(webCalendar, listOf()))),
            requestCalendarResponseFromApi(webCalendar).doOnNext {
                if (it.status == "Success") {
                    replaceSavedEvents(webCalendar, it.data.events)
                }
            }).onErrorReturnItem(ResponseWrapper.error(CalendarData(webCalendar, listOf()), "Other exception"))
    }

    private fun replaceSavedEvents(webCalendar: WebCalendar, events: List<CalendarEvent>) {

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

    override fun requestSavedData(): Observable<ResponseWrapper<MutableList<CalendarEvent>>> {
        return dataSource.getAllEventsSingle().map {
            val calendarResponse = ResponseWrapper.success(it.toMutableList())
            calendarResponse
        }.toObservable()
    }

    fun requestCalendarResponseFromApi(): Observable<ResponseWrapper<MutableList<CalendarEvent>>> {
        return apiService.getResponse().map {
            if (it.code() == 200 && it.body() != null) {
                val iCalString = it.body()!!.string()
                val events = iCalReader.getCalendarEvents(iCalString).toMutableList()
                events.sortBy(CalendarEvent::dateStart)
                val successResponse = ResponseWrapper.success(events)
                successResponse
            } else {
                ResponseWrapper.error(mutableListOf(), it.code().toString())
            }
        }
    }

    fun requestCalendarResponseFromApi(webCalendar: WebCalendar): Observable<ResponseWrapper<CalendarData>> {
        return apiService.getResponse(webCalendar.calendarUrl).map {
            if (it.code() == 200 && it.body() != null) {
                val iCalString = it.body()!!.string()
                val events = iCalReader.getCalendarEvents(iCalString).toMutableList()
                events.sortBy(CalendarEvent::dateStart)

                val calendarData = CalendarData(webCalendar, events)
                val successResponse = ResponseWrapper.success(calendarData)
                successResponse
            } else {
                ResponseWrapper.error(CalendarData(webCalendar, listOf()), it.code().toString())
            }
        }
    }

    override fun saveNewCalendar(calendarName: String, url: String) {
        val webCalendar = WebCalendar(calendarName = calendarName, calendarUrl = url)
        dataSource.insertCalendar(webCalendar)
    }

    override fun requestSavedCalendars(): Observable<List<WebCalendar>> {
        return Observable.just(dataSource.getAllCalendars())
    }

    override fun deleteCalendar(calendar: WebCalendar) {
        dataSource.deleteCalendar(calendar)
    }
}