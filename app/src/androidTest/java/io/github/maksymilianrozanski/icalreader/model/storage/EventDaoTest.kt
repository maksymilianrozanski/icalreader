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

    @Test
    fun getAllCalendarsSingleTest() {
        val webCalendar = WebCalendar(calendarName = "example calendar", calendarUrl = "http://example.com")
        database.eventDao().insertCalendar(webCalendar)

        database.eventDao().getAllCalendarsSingle().test()
            .assertValue { it[0] == webCalendar }
    }

    @Test
    fun deleteSingleCalendarTest() {
        val calendarOne = WebCalendar(calendarName = "first example calendar", calendarUrl = "http://example1.com")
        val calendarTwo = WebCalendar(calendarName = "second example calendar", calendarUrl = "http://example2.com")

        database.eventDao().insertCalendar(calendarOne)
        database.eventDao().insertCalendar(calendarTwo)

        Assert.assertEquals(2, database.eventDao().getAllCalendars().size)

        database.eventDao().deleteCalendar(calendarOne)

        val fetchedCalendars = database.eventDao().getAllCalendars()
        val fetchedCalendar = fetchedCalendars[0]
        Assert.assertEquals(calendarTwo.calendarId, fetchedCalendar.calendarId)
        Assert.assertEquals(calendarTwo.calendarName, fetchedCalendar.calendarName)
        Assert.assertEquals(calendarTwo.calendarUrl, fetchedCalendar.calendarUrl)
        Assert.assertEquals(1, fetchedCalendars.size)
    }

    @Test
    fun getEventsOfSpecificCalendarTest(){
        val calendarOne = WebCalendar(calendarName = "first example calendar", calendarUrl = "http://example1.com")
        val calendarTwo = WebCalendar(calendarName = "second example calendar", calendarUrl = "http://example2.com")
        val eventOne = CalendarEvent(
            title = "example title one",
            dateStart = Date(1999, 10, 10, 10, 10, 10),
            dateEnd = Date(1999, 11, 11, 11, 11, 11),
            location = "example location one",
            description = "example description one",
            calendarId = calendarOne.calendarId
        )
        val eventTwo = CalendarEvent(
            title = "example title two",
            dateStart = Date(2001, 10, 10, 10, 10, 10),
            dateEnd = Date(2001, 11, 11, 11, 11, 11),
            location = "example location two",
            description = "example description two",
            calendarId = calendarOne.calendarId
        )
        val eventThree = CalendarEvent(
            title = "example title three",
            dateStart = Date(2003, 10, 10, 10, 10, 10),
            dateEnd = Date(2003, 11, 11, 11, 11, 11),
            location = "example location three",
            description = "example description three",
            calendarId = calendarTwo.calendarId
        )

        database.eventDao().insertCalendar(calendarOne)
        database.eventDao().insertCalendar(calendarTwo)
        database.eventDao().insertEvent(eventOne)
        database.eventDao().insertEvent(eventTwo)
        database.eventDao().insertEvent(eventThree)

        val fetchedEventsCalendarOne = database.eventDao().getEventsOfCalendar(calendarOne.calendarId)
        Assert.assertEquals(2, fetchedEventsCalendarOne.size)
        Assert.assertEquals(listOf(eventOne, eventTwo), fetchedEventsCalendarOne)
        val fetchedEventsCalendarTwo = database.eventDao().getEventsOfCalendar(calendarTwo.calendarId)
        Assert.assertEquals(1, fetchedEventsCalendarTwo.size)
        Assert.assertEquals(eventThree, fetchedEventsCalendarTwo[0])
    }

    @Test
    fun deleteEventsOfSpecificCalendarTest() {
        val calendarOne = WebCalendar(calendarName = "first example calendar", calendarUrl = "http://example1.com")
        val calendarTwo = WebCalendar(calendarName = "second example calendar", calendarUrl = "http://example2.com")
        val eventOne = CalendarEvent(
            title = "example title one",
            dateStart = Date(1999, 10, 10, 10, 10, 10),
            dateEnd = Date(1999, 11, 11, 11, 11, 11),
            location = "example location one",
            description = "example description one",
            calendarId = calendarOne.calendarId
        )
        val eventTwo = CalendarEvent(
            title = "example title two",
            dateStart = Date(2001, 10, 10, 10, 10, 10),
            dateEnd = Date(2001, 11, 11, 11, 11, 11),
            location = "example location two",
            description = "example description two",
            calendarId = calendarTwo.calendarId
        )

        database.eventDao().insertCalendar(calendarOne)
        database.eventDao().insertCalendar(calendarTwo)
        database.eventDao().insertEvent(eventOne)
        database.eventDao().insertEvent(eventTwo)

        Assert.assertEquals(2, database.eventDao().getAllCalendars().size)
        Assert.assertEquals(1, database.eventDao().getEventsOfCalendar(calendarOne.calendarId).size)
        Assert.assertEquals(1, database.eventDao().getEventsOfCalendar(calendarTwo.calendarId).size)

        database.eventDao().deleteAllEventsOfCalendar(calendarOne.calendarId)

        Assert.assertEquals(0, database.eventDao().getEventsOfCalendar(calendarOne.calendarId).size)
        Assert.assertEquals(1, database.eventDao().getEventsOfCalendar(calendarTwo.calendarId).size)
    }
}