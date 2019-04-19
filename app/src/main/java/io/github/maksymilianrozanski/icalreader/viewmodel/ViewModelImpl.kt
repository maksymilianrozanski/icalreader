package io.github.maksymilianrozanski.icalreader.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
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

    val events: MutableLiveData<ResponseWrapper<MutableList<CalendarEvent>>> by lazy {
        MutableLiveData<ResponseWrapper<MutableList<CalendarEvent>>>()
    }

//    val eventsData: MutableLiveData<ResponseWrapper<CalendarData>> by lazy {
//        MutableLiveData<ResponseWrapper<CalendarData>>()
//    }

    val calendars: MutableLiveData<MutableList<WebCalendar>> by lazy {
        MutableLiveData<MutableList<WebCalendar>>()
    }

    private lateinit var subscription: Disposable
    private lateinit var calendarsSubscription: Disposable

    init {
        requestSavedCalendars()
        requestSavedData()
    }

//    private fun requestEventsData(){
//        calendarsSubscription = model.requestSavedCalendars()
//            .flatMap { model.requestSavedData(it[0]) }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                eventsData.value = it
//            }
//    }

    private fun requestSavedData() {
        subscription = model.requestSavedData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                events.value = it
            }
    }

    private fun requestSavedCalendars() {
        calendarsSubscription = model.requestSavedCalendars()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                calendars.value = it as MutableList<WebCalendar>
            }
    }

//    private fun requestSavedCalendarData(webCalendar: WebCalendar) {
//        subscription = model.requestSavedData(webCalendar)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                eventsData.value = it
//            }
//    }

    fun requestCalendarResponse() {
        subscription = model.requestNewData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { events.value = ResponseWrapper.loading(mutableListOf()) }
            .doOnError { println("Error") }
            .subscribe {
                events.value = it
            }
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
        calendarsSubscription.dispose()
    }
}