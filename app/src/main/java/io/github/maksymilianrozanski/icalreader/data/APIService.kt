package io.github.maksymilianrozanski.icalreader.data

import io.github.maksymilianrozanski.icalreader.CalendarEvent
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET


interface APIService {

    @GET("/events")
    fun getEvents(): Observable<List<CalendarEvent>>

    @GET("/")
    fun getHome(): Observable<ResponseBody>
}