package io.github.maksymilianrozanski.icalreader.module

import dagger.Module
import dagger.Provides
import java.util.*

@Module
class CalendarModule {

    @Provides
    fun provideCalendar(): Calendar {
        return Calendar.getInstance()
    }
}