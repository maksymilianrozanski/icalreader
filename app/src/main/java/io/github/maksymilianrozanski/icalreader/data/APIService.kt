package io.github.maksymilianrozanski.icalreader.data

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


interface APIService {

    @GET("/")
    fun getEvents(): Observable<List<CalendarEvent>>

    @GET("/")
    fun getHome(): Observable<Response<ResponseBody>>

    @GET("/api/test.ical")
    fun getResponse(): Observable<Response<ResponseBody>>

    @GET
    fun getResponse(@Url url: String): Observable<Response<ResponseBody>>
}