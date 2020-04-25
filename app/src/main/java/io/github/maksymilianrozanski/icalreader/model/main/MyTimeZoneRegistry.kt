package io.github.maksymilianrozanski.icalreader.model.main

import net.fortuna.ical4j.model.TimeZone
import net.fortuna.ical4j.model.TimeZoneRegistry
import net.fortuna.ical4j.model.component.VTimeZone

class MyTimeZoneRegistry : TimeZoneRegistry {
    override fun clear() {}

    override fun register(timezone: TimeZone?) {}

    override fun register(timezone: TimeZone?, update: Boolean) {}

    override fun getTimeZone(id: String?): TimeZone {
        return TimeZone(VTimeZone())
    }
}