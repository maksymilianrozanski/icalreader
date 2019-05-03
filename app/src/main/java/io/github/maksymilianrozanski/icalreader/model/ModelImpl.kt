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

    override fun requestCalendarData(webCalendar: WebCalendar): Observable<ResponseWrapper<CalendarData>> {
        return Observable.concatArray(
            Observable.just(ResponseWrapper.loading(CalendarData(webCalendar, mutableListOf()))),
            requestCalendarResponseFromApi(webCalendar).doOnNext {
                if (it.status == "Success") {
//Saving disabled//                    replaceSavedEvents(hardcodedCalendarToSave, it.data.events)
                }
            }).doOnError { println(it) }
            .onErrorReturnItem(ResponseWrapper.error(CalendarData(webCalendar, mutableListOf()), "Other exception"))
    }

    fun replaceSavedEvents(webCalendar: WebCalendar, events: List<CalendarEvent>) {
        val eventsCorrectIds = mutableListOf<CalendarEvent>()
        events.forEach {
            val event = CalendarEvent(
                calendarId = webCalendar.calendarId,
                title = it.title,
                dateStart = it.dateStart,
                dateEnd = it.dateEnd,
                description = it.description,
                location = it.location
            )
            eventsCorrectIds.add(event)
        }

        dataSource.deleteAllEventsOfCalendar(webCalendar.calendarId)
        dataSource.insertEventsList(eventsCorrectIds)
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


    override fun requestSavedData(webCalendar: WebCalendar): Observable<ResponseWrapper<CalendarData>> {
        return dataSource.getEventsOfCalendar(webCalendar.calendarId)
            .map {
                val calendarData = CalendarData(webCalendar, it.toMutableList())
                val response = ResponseWrapper.success(calendarData)
                response
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
                ResponseWrapper.error(CalendarData(webCalendar, mutableListOf()), it.code().toString())
            }
        }
    }

    override fun saveNewCalendar(calendarForm: CalendarForm): Observable<ResponseWrapper<CalendarForm>> {
        return if (isCalendarFormValid(calendarForm)) {
            val webCalendar =
                WebCalendar(calendarName = calendarForm.calendarName, calendarUrl = calendarForm.calendarUrl)
            Observable.concatArray(
                dataSource.insertCalendarSingle(webCalendar).toObservable(),
                Observable.just(ResponseWrapper.success(calendarForm))
            ).onErrorReturn {
                calendarForm.nameError = CalendarForm.databaseError
                ResponseWrapper.error(calendarForm, it.message)
            }
        } else {
            Observable.just(ResponseWrapper.error(calendarForm, "Invalid input"))
        }
    }

    private fun isCalendarFormValid(calendarForm: CalendarForm): Boolean {
        return calendarForm.isFormValid()
    }

    override fun requestSavedCalendars(): Observable<List<WebCalendar>> {
        return dataSource.getAllCalendarsSingle().toObservable()
    }

    override fun deleteCalendar(calendar: WebCalendar) {
        dataSource.deleteCalendar(calendar)
    }
}