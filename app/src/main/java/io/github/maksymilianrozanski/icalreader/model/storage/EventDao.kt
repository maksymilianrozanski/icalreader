package io.github.maksymilianrozanski.icalreader.model.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent

@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    fun getAllEvents(): List<CalendarEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: CalendarEvent)

    @Query("DELETE FROM events")
    fun deleteAllEvents()
}