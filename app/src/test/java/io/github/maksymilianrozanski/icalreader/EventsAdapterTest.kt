package io.github.maksymilianrozanski.icalreader

import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import org.junit.Assert
import org.junit.Test
import java.util.*

class EventsAdapterTest {

    @Test
    fun getPositionOfFirstNotFinishedEventTest() {
        val exampleTitle = "example title"
        val exampleDescription = "example description"
        val exampleLocation = "example location"

        val calendar = Calendar.getInstance()

        calendar.set(1999, 1, 1, 5, 0, 0)
        val dateStart1 = calendar.time
        calendar.set(1999, 1, 2, 5, 0, 0)
        val dateEnd1 = calendar.time

        calendar.set(1999, 2, 2, 10, 10, 10)
        val dateStart2 = calendar.time
        calendar.set(1999, 5, 3, 10, 0, 0)
        val dateEnd2 = calendar.time

        calendar.set(1999, 4, 2, 10, 0, 0)
        val dateStart3 = calendar.time
        calendar.set(1999, 10, 2, 14, 0, 0)
        val dateEnd3 = calendar.time

        calendar.set(2000, 2, 2, 13, 0, 0)
        val dateStart4 = calendar.time
        calendar.set(2000, 2, 2, 16, 0, 0)
        val dateEnd4 = calendar.time

        val event1 = CalendarEvent(
            dateStart = dateStart1,
            dateEnd = dateEnd1,
            title = exampleTitle,
            location = exampleLocation,
            description = exampleDescription
        )
        val event2 = CalendarEvent(
            dateStart = dateStart2,
            dateEnd = dateEnd2,
            title = exampleTitle,
            location = exampleLocation,
            description = exampleDescription
        )
        val event3 = CalendarEvent(
            dateStart = dateStart3,
            dateEnd = dateEnd3,
            title = exampleTitle,
            location = exampleLocation,
            description = exampleDescription
        )
        val event4 = CalendarEvent(
            dateStart = dateStart4,
            dateEnd = dateEnd4,
            title = exampleTitle,
            location = exampleLocation,
            description = exampleDescription
        )

        val events = mutableListOf(event1, event2, event3, event4)

        //before all
        calendar.set(1998, 2, 2, 10, 0, 0)
        Assert.assertEquals(0, getPositionOfFirstNotFinishedEvent(calendar, events))
        //during first
        calendar.set(1999, 1, 1, 10, 0, 0)
        Assert.assertEquals(0, getPositionOfFirstNotFinishedEvent(calendar, events))
        //after first
        calendar.set(1999, 1, 5, 10, 0, 0)
        Assert.assertEquals(1, getPositionOfFirstNotFinishedEvent(calendar, events))
        //during second
        calendar.set(1999, 2, 3, 14, 0, 0)
        Assert.assertEquals(1, getPositionOfFirstNotFinishedEvent(calendar, events))
        //during second and third
        calendar.set(1999, 4, 10, 10, 10, 10)
        Assert.assertEquals(1, getPositionOfFirstNotFinishedEvent(calendar, events))
        //after second, during third
        calendar.set(1999, 7, 2, 10, 12, 12)
        Assert.assertEquals(2, getPositionOfFirstNotFinishedEvent(calendar, events))
        //after third
        calendar.set(1999, 12, 24, 12, 0, 0)
        Assert.assertEquals(3, getPositionOfFirstNotFinishedEvent(calendar, events))
        //after all (should return index of last event)
        calendar.set(2013, 12, 12, 12, 0, 0)
        Assert.assertEquals(3, getPositionOfFirstNotFinishedEvent(calendar, events))
    }
}