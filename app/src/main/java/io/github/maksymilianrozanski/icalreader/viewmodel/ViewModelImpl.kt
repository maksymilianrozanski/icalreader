package io.github.maksymilianrozanski.icalreader.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.github.maksymilianrozanski.icalreader.data.CalendarData
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.model.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ViewModelImpl(application: Application) : BaseViewModel(application) {

    @Inject
    lateinit var model: Model

    val eventsData: MutableLiveData<ResponseWrapper<CalendarData>> by lazy {
        MutableLiveData<ResponseWrapper<CalendarData>>()
    }

    val calendars: MutableLiveData<MutableList<WebCalendar>> by lazy {
        MutableLiveData<MutableList<WebCalendar>>()
    }

    private lateinit var subscription: Disposable
    private lateinit var calendarsSubscription: Disposable

    init {
        requestSavedCalendars()
    }

    private fun requestSavedCalendars() {
        calendarsSubscription = model.requestSavedCalendars()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                calendars.value = it as MutableList<WebCalendar>
            }
    }

    fun requestCalendarResponse() {
        val webCalendarZero = calendars.value!![0]
        subscription = model.requestSavedCalendars().flatMap {
            model.requestCalendarData(it[0])
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                eventsData.value = ResponseWrapper.loading(CalendarData(webCalendarZero, mutableListOf()))
            }
            .doOnError { println("Error") }
            .subscribe {
                eventsData.value = it
            }
    }

    private fun requestCalendarResponse(webCalendar: WebCalendar) {
        subscription = model.requestCalendarData(webCalendar)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
}