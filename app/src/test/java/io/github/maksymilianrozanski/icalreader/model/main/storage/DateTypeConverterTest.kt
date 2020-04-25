package io.github.maksymilianrozanski.icalreader.model.main.storage

import org.junit.Assert
import org.junit.Test
import java.util.*

class DateTypeConverterTest {

    @Test
    fun toDate() {
        val input = 1554903116018L
        val converter = DateTypeConverter()
        val output = converter.toDate(input)
        val calendar = Calendar.getInstance()
        calendar.time = output
        calendar.timeZone = TimeZone.getTimeZone("GMT")

        Assert.assertEquals(2019, calendar.get(Calendar.YEAR))
        Assert.assertEquals(3, calendar.get(Calendar.MONTH))
        Assert.assertEquals(10, calendar.get(Calendar.DAY_OF_MONTH))
        Assert.assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY))
        Assert.assertEquals(31, calendar.get(Calendar.MINUTE))
        Assert.assertEquals(56, calendar.get(Calendar.SECOND))
    }
}