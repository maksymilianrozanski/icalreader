package io.github.maksymilianrozanski.icalreader.model.storage

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class EventDaoTest {

    private lateinit var database: EventsDatabase

    @Before
    fun initializeDb() {
        database =
            Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext, EventsDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun after() {
        database.close()
    }

    @Test
    fun getEventWhenZeroEvents() {
        val events = database.eventDao().getAllEvents()
        Assert.assertTrue(events.isEmpty())
    }

    @Test
    fun insertAndGetSingleEvent() {
        //TODO: replace deprecated Date constructor
        val event = CalendarEvent(
            title = "Example event title",
            dateStart = Date(2012, 3, 3, 10, 20, 20),
            dateEnd = Date(2012, 4, 4, 12, 20, 20),
            description = "example description",
            location = "example location"
        )

        database.eventDao().insertEvent(event)

        val obtainedEvent = database.eventDao().getAllEvents()[0]

        Assert.assertEquals(event, obtainedEvent)
    }

    @Test
    fun deleteEvents() {
        val event = CalendarEvent(
            title = "Example event title",
            dateStart = Date(2012, 3, 3, 10, 20, 20),
            dateEnd = Date(2012, 4, 4, 12, 20, 20),
            description = "example description",
            location = "example location"
        )

        database.eventDao().insertEvent(event)
        database.eventDao().deleteAllEvents()

        val events = database.eventDao().getAllEvents()
        Assert.assertTrue(events.isEmpty())
    }
}