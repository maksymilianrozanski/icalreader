package io.github.maksymilianrozanski.icalreader.viewmodel

import android.arch.lifecycle.MutableLiveData
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.model.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ViewModelImpl : BaseViewModel() {

    @Inject
    lateinit var model: Model

    val events: MutableLiveData<MutableList<CalendarEvent>> by lazy {
        MutableLiveData<MutableList<CalendarEvent>>()
    }

    private lateinit var subscription: Disposable

    init {
        requestEvents()
    }

    private fun requestEvents() {
        subscription = model.requestEvents().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { println("Error") }
            .subscribe {
                events.value = it.toMutableList()
            }
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }
}