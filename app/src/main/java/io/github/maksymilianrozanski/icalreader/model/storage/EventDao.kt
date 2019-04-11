package io.github.maksymilianrozanski.icalreader.model.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.reactivex.Single

@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    fun getAllEvents(): List<CalendarEvent>

    @Query("SELECT * FROM events")
    fun getAllEventsSingle(): Single<List<CalendarEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: CalendarEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEventsList(events: List<CalendarEvent>)

    @Query("DELETE FROM events")
    fun deleteAllEvents()
}