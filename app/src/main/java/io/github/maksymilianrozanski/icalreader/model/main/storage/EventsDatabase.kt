package io.github.maksymilianrozanski.icalreader.model.main.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.WebCalendar

@Database(entities = [WebCalendar::class, CalendarEvent::class], version = 2)
@TypeConverters(DateTypeConverter::class)
abstract class EventsDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {

        @Volatile
        private var INSTANCE: EventsDatabase? = null

        fun getInstance(context: Context): EventsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, EventsDatabase::class.java, "EventsBase.db").build()
    }
}