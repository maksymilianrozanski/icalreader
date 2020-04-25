package io.github.maksymilianrozanski.icalreader.data

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NumbersAPIService {

    @GET("{month}/{day}")
    fun getWhatHappenedThisDay(
        @Path("month") month: Int,
        @Path("day") day: Int
    ): Observable<Response<ResponseBody>>
}