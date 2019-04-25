package io.github.maksymilianrozanski.icalreader.data

import org.junit.Assert
import org.junit.Test

class CalendarFormTest {

    @Test
    fun blankInputTest() {
        val name = "name"
        val url = "http://example.com"
        val calendarForm = CalendarForm(name, url)
        Assert.assertEquals(name, calendarForm.calendarName)
        Assert.assertEquals(url, calendarForm.calendarUrl)
        Assert.assertTrue(calendarForm.nameError == null)
        Assert.assertTrue(calendarForm.urlError == null)

        calendarForm.calendarName = ""
        Assert.assertEquals("", calendarForm.calendarName)
        Assert.assertEquals(url, calendarForm.calendarUrl)
        Assert.assertTrue(calendarForm.nameError == "Cannot be blank")
        Assert.assertTrue(calendarForm.urlError == null)

        calendarForm.calendarUrl = ""
        Assert.assertEquals("", calendarForm.calendarName)
        Assert.assertEquals("", calendarForm.calendarUrl)
        Assert.assertTrue(calendarForm.nameError == "Cannot be blank")
        Assert.assertTrue(calendarForm.urlError == "Cannot be blank")

        calendarForm.calendarName = "another name"
        Assert.assertEquals("another name", calendarForm.calendarName)
        Assert.assertEquals("", calendarForm.calendarUrl)
        Assert.assertTrue(calendarForm.nameError == null)
        Assert.assertTrue(calendarForm.urlError == "Cannot be blank")

        calendarForm.calendarUrl = "http://example2.com"
        Assert.assertEquals("another name", calendarForm.calendarName)
        Assert.assertEquals("http://example2.com", calendarForm.calendarUrl)
        Assert.assertTrue(calendarForm.nameError == null)
        Assert.assertTrue(calendarForm.urlError == null)
    }
}