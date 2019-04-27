package io.github.maksymilianrozanski.icalreader.module

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.model.storage.EventDao
import io.github.maksymilianrozanski.icalreader.model.storage.EventsDatabase
import javax.inject.Singleton

@Module
class DatabaseTestModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): EventsDatabase {
        val database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, EventsDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        database.eventDao().insertCalendar(WebCalendar(calendarName = "Calendar mock 1", calendarUrl = "api/test.ical"))
        database.eventDao().insertCalendar(WebCalendar(calendarName = "Calendar mock 2", calendarUrl = "someurl.com"))
        database.eventDao().insertCalendar(WebCalendar(calendarName = "Calendar mock 3", calendarUrl = "example.com"))
        return database
    }

    @Singleton
    @Provides
    fun provideEventsDao(database: EventsDatabase): EventDao {
        return database.eventDao()
    }
}