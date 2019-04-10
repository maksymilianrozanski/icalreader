package io.github.maksymilianrozanski.icalreader.module

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.icalreader.model.storage.EventDao
import io.github.maksymilianrozanski.icalreader.model.storage.EventsDatabase
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): EventsDatabase {
        return EventsDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideEventsDao(database: EventsDatabase): EventDao {
        return database.eventDao()
    }
}