package io.github.maksymilianrozanski.icalreader.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.github.maksymilianrozanski.icalreader.data.CalendarData
import io.github.maksymilianrozanski.icalreader.data.CalendarForm
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.model.Model
import io.github.maksymilianrozanski.icalreader.module.NetworkModule
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
        val webCalendarZero = calendars.value!![0]
        subscription = model.requestSavedCalendars().flatMap {
            model.requestCalendarData(it[0])
        }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doOnSubscribe {
                eventsData.value = ResponseWrapper.loading(CalendarData(webCalendarZero, mutableListOf()))
            }
            .doOnError { println("Error") }
            .subscribe {
                eventsData.value = it
            }
    }

    override fun saveNewCalendar(formToSave: CalendarForm) {
        calendarsSubscription = model.saveNewCalendar(formToSave)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribeBy(
                onNext = {
                    calendarForm.postValue(it.data)
                },
                onError = {
                    formToSave.nameError = CalendarForm.unknownError
                    calendarForm.postValue(formToSave)
                }
            )
    }

    override fun saveNewCalendar() {
        calendarsSubscription = model.saveNewCalendar(hardcodedCalendarToSave)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe {
                calendars.postValue(it as MutableList<WebCalendar>)
            }
    }

    private fun requestCalendarResponse(webCalendar: WebCalendar) {
        subscription = model.requestCalendarData(webCalendar)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doOnSubscribe {
                eventsData.value = ResponseWrapper.loading(CalendarData(webCalendar, mutableListOf()))
            }
            .doOnError { println("Error") }
            .subscribe {
                eventsData.value = it
            }
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