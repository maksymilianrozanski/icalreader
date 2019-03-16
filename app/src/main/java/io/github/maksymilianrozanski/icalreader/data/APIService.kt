package io.github.maksymilianrozanski.icalreader.data

import io.github.maksymilianrozanski.icalreader.CalendarEvent
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET


interface APIService {

    @GET("/")
    fun getEvents(): Observable<List<CalendarEvent>>

    @GET("/")
    fun getHome(): Observable<Response<ResponseBody>>

    @GET("/api/test.ical")
    fun getResponse(): Observable<Response<ResponseBody>>

}