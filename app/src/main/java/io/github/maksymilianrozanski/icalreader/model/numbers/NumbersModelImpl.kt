package io.github.maksymilianrozanski.icalreader.model.numbers

import arrow.core.Either
import io.github.maksymilianrozanski.icalreader.data.NumbersAPIService
import io.github.maksymilianrozanski.icalreader.data.NumbersApiResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Error

object NumbersModelImpl : NumbersModel {

    fun responseFromApi(api: NumbersAPIService): (Int, Int) -> Observable<Response<ResponseBody>> =
        { month: Int, day: Int -> api.getWhatHappenedThisDay(month = month, day = day) }

    override fun requestCalendarData(data: (Int, Int) -> Observable<Either<Error, NumbersApiResponse>>) {
        TODO("Not yet implemented")
    }

}