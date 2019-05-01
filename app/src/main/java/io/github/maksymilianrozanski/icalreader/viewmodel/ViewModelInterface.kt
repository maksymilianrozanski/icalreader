package io.github.maksymilianrozanski.icalreader.viewmodel

import androidx.lifecycle.MutableLiveData
import io.github.maksymilianrozanski.icalreader.data.CalendarData
import io.github.maksymilianrozanski.icalreader.data.CalendarForm
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar

interface ViewModelInterface {

    val eventsData: MutableLiveData<ResponseWrapper<CalendarData>>
    val calendars: MutableLiveData<MutableList<WebCalendar>>
    val calendarForm: MutableLiveData<CalendarForm>
    fun requestCalendarResponse()
    fun requestCalendarResponse(webCalendar: WebCalendar)
    fun requestSavedCalendarData(webCalendar: WebCalendar)
    fun saveNewCalendar(formToSave: CalendarForm)
}