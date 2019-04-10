package io.github.maksymilianrozanski.icalreader.model.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent

@Database(entities = [CalendarEvent::class], version = 1)
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