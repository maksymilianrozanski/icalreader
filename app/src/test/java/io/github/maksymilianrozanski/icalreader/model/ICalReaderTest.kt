package io.github.maksymilianrozanski.icalreader.model

import net.fortuna.ical4j.validate.ValidationException
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ICalReaderTest {

    @Test
    fun getCalendarEventsSingleEventTest() {
        val inputICalString = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//hacksw/handcal//NONSGML v1.0//EN\n" +
                "BEGIN:VEVENT\n" +
                "UID:12345my calendar\n" +
                "DTSTAMP:20190228T215857Z\n" +
                "DTSTART:19970714T170000Z\n" +
                "DTEND:19970715T035959Z\n" +
                "SUMMARY:Bastille Day Party\n" +
                "DESCRIPTION:This is example event description.\n" +
                "LOCATION:Warsaw\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR"

        val iCalReader = ICalReader()
        val output = iCalReader.getCalendarEvents(inputICalString)
        val indexZeroEvent = output[0]

        assertTrue(indexZeroEvent.title == "Bastille Day Party")
        assertTrue(indexZeroEvent.dateStart == "19970714T170000Z")
        assertTrue(indexZeroEvent.dateEnd == "19970715T035959Z")
        assertTrue(indexZeroEvent.description == "This is example event description.")
        assertTrue(indexZeroEvent.location == "Warsaw")
    }

    @Test
    fun getCalendarEventEmptyValuesTest() {
        val inputICalString = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//hacksw/handcal//NONSGML v1.0//EN\n" +
                "BEGIN:VEVENT\n" +
                "UID:12345my calendar\n" +
                "DTSTAMP:20190228T215857Z\n" +
                "DTSTART:19970714T170000Z\n" +
                "DTEND:19970715T035959Z\n" +
                "SUMMARY:\n" +
                "DESCRIPTION:\n" +
                "LOCATION:\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR"

        val iCalReader = ICalReader()
        val output = iCalReader.getCalendarEvents(inputICalString)
        val indexZeroEvent = output[0]

        assertTrue(indexZeroEvent.title == "")
        assertTrue(indexZeroEvent.dateStart == "19970714T170000Z")
        assertTrue(indexZeroEvent.dateEnd == "19970715T035959Z")
        assertTrue(indexZeroEvent.description == "")
        assertTrue(indexZeroEvent.location == "")
    }

    @Test
    fun getCalendarEventNoStartDateTest() {
        val inputICalString = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//hacksw/handcal//NONSGML v1.0//EN\n" +
                "BEGIN:VEVENT\n" +
                "UID:12345my calendar\n" +
                "DTSTAMP:20190228T215857Z\n" +

                "DTEND:19970715T035959Z\n" +
                "SUMMARY:Bastille Day Party\n" +
                "DESCRIPTION:This is example event description.\n" +
                "LOCATION:Warsaw\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR"

        val iCalReader = ICalReader()
        val exceptionMessage = assertFailsWith(ValidationException::class) {
            iCalReader.getCalendarEvents(inputICalString)
        }.message

        assertEquals(
            exceptionMessage, "DTSTART or DTEND property not available. isNull: DTSTART:true, DTEND:false"
        )
    }

    @Test
    fun getCalendarEventNoEndDateTest(){
        val inputICalString = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//hacksw/handcal//NONSGML v1.0//EN\n" +
                "BEGIN:VEVENT\n" +
                "UID:12345my calendar\n" +
                "DTSTAMP:20190228T215857Z\n" +
                "DTSTART:19970714T170000Z\n" +

                "SUMMARY:Bastille Day Party\n" +
                "DESCRIPTION:This is example event description.\n" +
                "LOCATION:Warsaw\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR"

        val iCalReader = ICalReader()
        val exceptionMessage = assertFailsWith(ValidationException::class) {
            iCalReader.getCalendarEvents(inputICalString)
        }.message

        assertEquals(
            exceptionMessage, "DTSTART or DTEND property not available. isNull: DTSTART:false, DTEND:true"
        )
    }
}