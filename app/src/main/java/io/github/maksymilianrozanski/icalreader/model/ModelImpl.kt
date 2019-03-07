package io.github.maksymilianrozanski.icalreader.model

import io.reactivex.Observable

class ModelImpl : Model {

    override fun requestData(): Observable<String> {
        Thread.sleep(1000)
        return Observable.just("some text" + System.currentTimeMillis())
    }
}