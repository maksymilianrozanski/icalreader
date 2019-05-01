package io.github.maksymilianrozanski.icalreader.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.github.maksymilianrozanski.icalreader.data.CalendarData
import io.github.maksymilianrozanski.icalreader.data.CalendarForm
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.model.Model
import io.github.maksymilianrozanski.icalreader.module.NetworkModule
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ViewModelImpl(application: Application) : BaseViewModel(application), ViewModelInterface {

    @Inject
    lateinit var model: Model

    @Inject
    lateinit var schedulerProvider: BaseSchedulerProvider

    override val eventsData: MutableLiveData<ResponseWrapper<CalendarData>> by lazy {
        MutableLiveData<ResponseWrapper<CalendarData>>()
    }

    override val calendars: MutableLiveData<MutableList<WebCalendar>> by lazy {
        MutableLiveData<MutableList<WebCalendar>>()
    }

    override val calendarForm: MutableLiveData<CalendarForm> by lazy {
        MutableLiveData<CalendarForm>()
    }

    private lateinit var subscription: Disposable
    lateinit var calendarsSubscription: Disposable

    init {
        requestSavedCalendars()
    }

    private fun requestSavedCalendars() {
        calendarsSubscription =
            model.requestSavedCalendars()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe {
                    calendars.postValue(it as MutableList<WebCalendar>)
                }
    }

    override fun requestCalendarResponse() {
        requestCalendarResponse(eventsData.value?.data?.webCalendar ?: calendars.value?.get(0) ?: return)
    }

    override fun requestCalendarResponse(webCalendar: WebCalendar) {
        subscription = subscribeToCalendarData(webCalendar, model.requestCalendarData(webCalendar))
    }

    override fun requestSavedCalendarData(webCalendar: WebCalendar) {
        subscription = subscribeToCalendarData(webCalendar, model.requestSavedData(webCalendar))
    }

    private fun subscribeToCalendarData(
        requestedCalendar: WebCalendar,
        observable: Observable<ResponseWrapper<CalendarData>>
    ): Disposable {
        return observable.subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doOnSubscribe {
                if (eventsData.value?.data?.webCalendar?.calendarId ?: false == requestedCalendar.calendarId) {
                    eventsData.postValue(
                        ResponseWrapper.loading(
                            CalendarData(
                                requestedCalendar,
                                eventsData.value?.data?.events ?: mutableListOf()
                            )
                        )
                    )
                } else {
                    eventsData.postValue(ResponseWrapper.loading(CalendarData(requestedCalendar, mutableListOf())))
                }
            }
            .doOnError { println("Error") }
            .subscribe {
                if (eventsData.value?.data?.webCalendar?.calendarId ?: false == requestedCalendar.calendarId) {
                    postNewEventsIfAvailable(it)
                } else {//displaying other calendar
                    eventsData.postValue(it)
                }
            }
    }

    private fun postNewEventsIfAvailable(response: ResponseWrapper<CalendarData>) {
        when (response.data.events.size > 0) {
            true -> eventsData.postValue(response)
            false -> {
                when (response.status) {
                    "Success" -> eventsData.postValue(response)
                    "Error" -> eventsData.postValue(
                        ResponseWrapper.error(
                            CalendarData(
                                response.data.webCalendar, eventsData.value?.data?.events ?: mutableListOf()
                            ),
                            response.message
                        )
                    )
                    "Loading" -> eventsData.postValue(
                        ResponseWrapper.loading(
                            CalendarData(
                                response.data.webCalendar,
                                eventsData.value?.data?.events ?: mutableListOf()
                            )
                        )
                    )
                }
            }
        }
    }

    override fun saveNewCalendar(formToSave: CalendarForm) {
        calendarsSubscription = model.saveNewCalendar(formToSave)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribeBy(
                onNext = {
                    calendarForm.postValue(it.data)
                    requestSavedCalendars()
                },
                onError = {
                    formToSave.nameError = CalendarForm.unknownError
                    calendarForm.postValue(formToSave)
                }
            )
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
        calendarsSubscription.dispose()
    }

    companion object {
        val hardcodedCalendarToSave =
            WebCalendar(calendarName = "Calendar mock 1", calendarUrl = NetworkModule.baseUrl + "api/test.ical")
    }
}