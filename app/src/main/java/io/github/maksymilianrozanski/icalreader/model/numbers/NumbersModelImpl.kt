package io.github.maksymilianrozanski.icalreader.model.numbers

import arrow.core.Either
import io.github.maksymilianrozanski.icalreader.data.NumbersAPIService
import io.github.maksymilianrozanski.icalreader.data.NumbersApiResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Error

object NumbersModelImpl {

    fun responseFromApi(api: NumbersAPIService): (Int, Int) -> Observable<Response<ResponseBody>> =
        { month: Int, day: Int -> api.getWhatHappenedThisDay(month = month, day = day) }

    fun requestCalendarData(data: (Int, Int) -> Observable<Either<Error, NumbersApiResponse>>) {
        TODO("Not yet implemented")
    }

    fun validate(response: Observable<Response<NumbersApiResponse>>) {
        TODO()
    }

    fun <T> isSuccessful(response: Response<T>): Either<NumbersApiError, Response<T>> {
        return if (response.code() == 200) Either.Right(response)
        else Either.Left(NumbersApiError(response.code()))
    }
}

data class NumbersApiError(val responseCode: Int)