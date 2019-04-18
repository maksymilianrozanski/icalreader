package io.github.maksymilianrozanski.icalreader.model.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.reactivex.Single

@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    fun getAllEvents(): List<CalendarEvent>

    @Query("SELECT * FROM calendars")
    fun getAllCalendars():List<WebCalendar>

    @Query("SELECT * FROM events ORDER BY datestart")
    fun getAllEventsSingle(): Single<List<CalendarEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCalendar(calendar: WebCalendar)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: CalendarEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEventsList(events: List<CalendarEvent>)

    @Query("DELETE FROM events")
    fun deleteAllEvents()

    @Query("DELETE FROM calendars")
    fun deleteAllCalendars()
}