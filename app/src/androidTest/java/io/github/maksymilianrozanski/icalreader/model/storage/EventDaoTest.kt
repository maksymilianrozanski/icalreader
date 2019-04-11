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
        val event = CalendarEvent(
            title = "Example event title",
            dateStart = Date(1554883200L),
            dateEnd = Date(1554907018L),
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
            dateStart = Date(1554883200L),
            dateEnd = Date(1554907018L),
            description = "example description",
            location = "example location"
        )

        database.eventDao().insertEvent(event)
        database.eventDao().deleteAllEvents()

        val events = database.eventDao().getAllEvents()
        Assert.assertTrue(events.isEmpty())
    }

    @Test
    fun getAllEventsSingleTest(){
        val eventOne = CalendarEvent(
            title = "Example event title",
            dateStart = Date(1554883200L),
            dateEnd = Date(1554907018L),
            description = "example description",
            location = "example location"
        )

        val eventTwo = CalendarEvent(
            title = "Second example title",
            dateStart = Date(1554990258504L),
            dateEnd = Date(1554990267619L),
            description = "Second example description",
            location = "Second example location"
        )

        val events = listOf(eventOne, eventTwo)

        database.eventDao().insertEventsList(events)

        database.eventDao().getAllEventsSingle().test()
            .assertValue { it.containsAll(events) && events.containsAll(it)}
    }
}