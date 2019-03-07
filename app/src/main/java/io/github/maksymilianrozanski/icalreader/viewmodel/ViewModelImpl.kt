package io.github.maksymilianrozanski.icalreader.viewmodel

import android.arch.lifecycle.MutableLiveData
import io.github.maksymilianrozanski.icalreader.data.APIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ViewModelImpl : BaseViewModel() {
    @Inject
    lateinit var api: APIService

    val helloWorldData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private lateinit var subscription: Disposable

    init {
        loadPosts()
    }

    private fun loadPosts() {
//        subscription = api.getEvents()
        subscription = api.getHome()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrievePostListStart() }
            .doOnTerminate { onRetrievePostListFinish() }
            .subscribe(
                { onRetrievePostListSuccess() },
                { onRetrievePostListError() }
            )
    }

    private fun onRetrievePostListStart() {
        println("inside onRetrievePostListStart")
    }

    private fun onRetrievePostListFinish() {
        println("inside onRetrievePostListFinish, changing helloWorldData")
        helloWorldData.value = "Value has been changed by ViewModel"
    }

    private fun onRetrievePostListSuccess() {
        println("inside onRetrievePostListSuccess")
    }

    private fun onRetrievePostListError() {
        println("inside onRetrievePostListError")
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }
}