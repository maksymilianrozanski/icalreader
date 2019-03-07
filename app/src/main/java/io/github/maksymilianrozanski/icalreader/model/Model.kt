package io.github.maksymilianrozanski.icalreader.model

import io.reactivex.Observable

interface Model {

    fun requestData(): Observable<String>
}