package io.github.maksymilianrozanski.icalreader.module

import dagger.Module
import dagger.Provides
import java.util.*

@Module
class CalendarTestModule(val calendarMock:Calendar){

    @Provides
    fun provideCalendar():Calendar{
       return calendarMock
    }
}