package io.github.maksymilianrozanski.icalreader.model.numbers

import arrow.core.Either
import io.github.maksymilianrozanski.icalreader.data.NumbersApiResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Error

interface NumbersModel {

    fun requestCalendarData(data: ((Int, Int) -> Observable<Either<Error, NumbersApiResponse>>))
}