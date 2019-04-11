package io.github.maksymilianrozanski.icalreader.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.CalendarResponse
import io.github.maksymilianrozanski.icalreader.model.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ViewModelImpl(application: Application) : BaseViewModel(application) {

    @Inject
    lateinit var model: Model

    val events: MutableLiveData<CalendarResponse<MutableList<CalendarEvent>>> by lazy {
        MutableLiveData<CalendarResponse<MutableList<CalendarEvent>>>()
    }

    private lateinit var subscription: Disposable

    init {
        requestCalendarResponse()
    }

    fun requestCalendarResponse() {
        subscription = model.requestCalendarResponse()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { events.value = CalendarResponse.loading(mutableListOf()) }
            .doOnError { println("Error") }
            .subscribe {
                events.value = it
            }

    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }
}