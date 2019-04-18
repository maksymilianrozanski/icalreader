package io.github.maksymilianrozanski.icalreader.model.storage

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
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
        val webCalendar = WebCalendar(calendarName = "example calendar", calendarUrl = "http://example.com")
        database.eventDao().insertCalendar(webCalendar)

        val event = CalendarEvent(
            calendarId = webCalendar.calendarId,
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
        val webCalendar = WebCalendar(calendarName = "example calendar", calendarUrl = "http://example.com")
        database.eventDao().insertCalendar(webCalendar)

        val event = CalendarEvent(
            calendarId = webCalendar.calendarId,
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
    fun getAllEventsSingleTest() {
        val webCalendar = WebCalendar(calendarName = "example calendar", calendarUrl = "http://example.com")
        database.eventDao().insertCalendar(webCalendar)

        val oldestEvent = CalendarEvent(
            calendarId = webCalendar.calendarId,
            title = "Example event title",
            dateStart = Date(915181810000L),
            dateEnd = Date(915199810000L),
            description = "example description",
            location = "example location"
        )

        val middleEvent = CalendarEvent(
            calendarId = webCalendar.calendarId,
            title = "Other example event title",
            dateStart = Date(946735810000L),
            dateEnd = Date(946761010000L),
            description = "example description",
            location = "example location"
        )

        val mostRecentEvent = CalendarEvent(
            calendarId = webCalendar.calendarId,
            title = "Another example title",
            dateStart = Date(1554990258504L),
            dateEnd = Date(1554990267619L),
            description = "Second example description",
            location = "Second example location"
        )

        val unsortedEvents = listOf(middleEvent, oldestEvent, mostRecentEvent)

        database.eventDao().insertEventsList(unsortedEvents)

        database.eventDao().getAllEventsSingle().test()
            .assertValue { it == unsortedEvents.sortedBy(CalendarEvent::dateStart) }
    }

    @Test
    fun cascadeDeletionTest() {
        val webCalendar = WebCalendar(calendarName = "example calendar", calendarUrl = "http://example.com")
        database.eventDao().insertCalendar(webCalendar)

        val event = CalendarEvent(
            calendarId = webCalendar.calendarId,
            title = "Example event title",
            dateStart = Date(1554883200L),
            dateEnd = Date(1554907018L),
            description = "example description",
            location = "example location"
        )

        database.eventDao().insertEvent(event)

        var fetchedCalendars = database.eventDao().getAllCalendars()
        Assert.assertEquals(1, fetchedCalendars.size)

        var fetchedEvents = database.eventDao().getAllEvents()
        Assert.assertEquals(1, fetchedEvents.size)

        database.eventDao().deleteAllCalendars()
        fetchedCalendars = database.eventDao().getAllCalendars()
        Assert.assertEquals(0, fetchedCalendars.size)
        fetchedEvents = database.eventDao().getAllEvents()
        Assert.assertEquals(0, fetchedEvents.size)
    }
}