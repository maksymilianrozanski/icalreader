package io.github.maksymilianrozanski.icalreader.model

import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.data.CalendarParserImpl
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.validate.ValidationException
import org.apache.commons.lang3.time.DateUtils
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.Objects.isNull

class ICalReaderImpl : ICalReader {

    override fun getCalendarEvents(inputICal: String): List<CalendarEvent> {

        val stringReader = StringReader(inputICal)
        val calendarBuilder = CalendarBuilder(CalendarParserImpl(), MyTimeZoneRegistry())
        val calendar: Calendar = calendarBuilder.build(stringReader)

        val components = calendar.components
        val calendarEvents = mutableListOf<CalendarEvent>()

        components.forEach {
            var title = ""
            if (it.getProperty<Property>(Property.SUMMARY) != null) {
                title = it.getProperty<Property>(Property.SUMMARY).value
            }
            val start: Date
            val end: Date
            if (it.getProperty<Property>(Property.DTSTART) != null && it.getProperty<Property>(Property.DTEND) != null) {
                start = toWarsawTimeZone(it.getProperty<Property>(Property.DTSTART).value)
                end = toWarsawTimeZone(it.getProperty<Property>(Property.DTEND).value)
            } else {
                throw ValidationException(
                    "DTSTART or DTEND property not available. isNull: DTSTART:${
                    isNull(it.getProperty<Property>(Property.DTSTART))}, DTEND:${isNull(
                        it.getProperty<Property>(
                            Property.DTEND
                        )
                    )}"
                )
            }

            var description = ""
            if (it.getProperty<Property>(Property.DESCRIPTION) != null) {
                description = it.getProperty<Property>(Property.DESCRIPTION).value
            }

            var location = ""
            if (it.getProperty<Property>(Property.LOCATION) != null) {
                location = it.getProperty<Property>(Property.LOCATION).value
            }

            calendarEvents.add(
                CalendarEvent(title, start, end, description, location)
            )
        }
        return calendarEvents
    }
}

fun toWarsawTimeZone(input: String): Date {
    val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    val date = dateFormat.parse(input)
    return DateUtils.addHours(date, 1)
}