package io.github.maksymilianrozanski.icalreader.module

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.icalreader.model.storage.EventDao
import io.github.maksymilianrozanski.icalreader.model.storage.EventsDatabase
import javax.inject.Singleton

@Module
class DatabaseTestModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): EventsDatabase {
        return Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, EventsDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideEventsDao(database: EventsDatabase): EventDao {
        return database.eventDao()
    }
}