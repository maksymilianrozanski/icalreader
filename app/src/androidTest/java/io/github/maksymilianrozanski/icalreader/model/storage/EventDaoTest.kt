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
    fun deleteSingleCalendarCompletableTest() {
        val calendarOne = WebCalendar(calendarName = "first example calendar", calendarUrl = "http://example1.com")
        val calendarTwo = WebCalendar(calendarName = "second example calendar", calendarUrl = "http://example2.com")

        database.eventDao().insertCalendar(calendarOne)
        database.eventDao().insertCalendar(calendarTwo)

        Assert.assertEquals(2, database.eventDao().getAllCalendars().size)

        database.eventDao().deleteCalendarCompletable(calendarOne).test().await().assertNoErrors().assertComplete()

        val fetchedCalendars = database.eventDao().getAllCalendars()
        val fetchedCalendar = fetchedCalendars[0]
        Assert.assertEquals(calendarTwo.calendarId, fetchedCalendar.calendarId)
        Assert.assertEquals(calendarTwo.calendarName, fetchedCalendar.calendarName)
        Assert.assertEquals(calendarTwo.calendarUrl, fetchedCalendar.calendarUrl)
        Assert.assertEquals(1, fetchedCalendars.size)
    }

    @Test
    fun deleteSingleCalendarCascadeDeletionTest() {
        val calendarOne = WebCalendar(calendarName = "first example calendar", calendarUrl = "http://example1.com")
        val eventOne = CalendarEvent(
            calendarId = calendarOne.calendarId,
            title = "First title",
            location = "location",
            dateStart = Date(1544613132000L),
            dateEnd = Date(1544613192000L),
            description = "description"
        )
        val calendarTwo = WebCalendar(calendarName = "second example calendar", calendarUrl = "http://example2.com")
        val eventTwo = CalendarEvent(
            calendarId = calendarTwo.calendarId,
            title = "Second title",
            location = "Location",
            dateStart = Date(1542017592000L),
            dateEnd = Date(1542017652000L),
            description = "description"
        )

        database.eventDao().insertCalendar(calendarOne)
        database.eventDao().insertEvent(eventOne)
        database.eventDao().insertCalendar(calendarTwo)
        database.eventDao().insertEvent(eventTwo)

        Assert.assertEquals(2, database.eventDao().getAllCalendars().size)
        database.eventDao().deleteCalendarCompletable(calendarOne).test().await().assertNoErrors().assertComplete()

        val fetchedCalendars = database.eventDao().getAllCalendars()
        val fetchedCalendar = fetchedCalendars[0]
        Assert.assertEquals(calendarTwo.calendarId, fetchedCalendar.calendarId)
        Assert.assertEquals(calendarTwo.calendarName, fetchedCalendar.calendarName)
        Assert.assertEquals(calendarTwo.calendarUrl, fetchedCalendar.calendarUrl)
        Assert.assertEquals(1, fetchedCalendars.size)

        database.eventDao().getAllEventsSingle().test().await().assertNoErrors()
            .assertValue { it.size == 1 && it[0].title == "Second title" }
    }

    @Test
    fun getEventsOfSpecificCalendarSingleTest() {
        val calendarOne = WebCalendar(calendarName = "first example calendar", calendarUrl = "http://example1.com")
        val calendarTwo = WebCalendar(calendarName = "second example calendar", calendarUrl = "http://example2.com")
        val eventOne = CalendarEvent(
            title = "example title one",
            dateStart = Date(915181810000L),
            dateEnd = Date(915189010000L),
            location = "example location one",
            description = "example description one",
            calendarId = calendarOne.calendarId
        )
        val eventTwo = CalendarEvent(
            title = "example title two",
            dateStart = Date(946725010000L),
            dateEnd = Date(946728610000L),
            location = "example location two",
            description = "example description two",
            calendarId = calendarOne.calendarId
        )
        val eventThree = CalendarEvent(
            title = "example title three",
            dateStart = Date(1009887010000L),
            dateEnd = Date(1009897810000L),
            location = "example location three",
            description = "example description three",
            calendarId = calendarTwo.calendarId
        )

        database.eventDao().insertCalendar(calendarOne)
        database.eventDao().insertCalendar(calendarTwo)
        database.eventDao().insertEvent(eventOne)
        database.eventDao().insertEvent(eventTwo)
        database.eventDao().insertEvent(eventThree)

        database.eventDao().getEventsOfCalendar(calendarOne.calendarId).test().await().assertNoErrors()
            .assertValue {
                listOf(eventOne, eventTwo) == it
            }
        database.eventDao().getEventsOfCalendar(calendarTwo.calendarId).test().await().assertNoErrors()
            .assertValue {
                listOf(eventThree) == it
            }
    }

    @Test
    fun getEventsOfSpecificCalendarSingleNoEventsTest() {
        val calendarOne = WebCalendar(calendarName = "first example calendar", calendarUrl = "http://example1.com")
        database.eventDao().insertCalendar(calendarOne)

        database.eventDao().getEventsOfCalendar(calendarOne.calendarId).test().await().assertNoErrors()
            .assertValue { listOf<CalendarEvent>() == it }
    }
}