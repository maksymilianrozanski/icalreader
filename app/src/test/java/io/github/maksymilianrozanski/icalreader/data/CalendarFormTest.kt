package io.github.maksymilianrozanski.icalreader.data

import io.github.maksymilianrozanski.icalreader.data.CalendarForm.Companion.cannotBeBlank
import io.github.maksymilianrozanski.icalreader.data.CalendarForm.Companion.cannotContainSpaces
import io.github.maksymilianrozanski.icalreader.data.CalendarForm.Companion.cannotEndWithDot
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
        Assert.assertTrue(calendarForm.nameStatus == null)
        Assert.assertTrue(calendarForm.urlStatus == null)

        calendarForm.calendarName = ""
        Assert.assertEquals("", calendarForm.calendarName)
        Assert.assertEquals(url, calendarForm.calendarUrl)
        Assert.assertTrue(calendarForm.nameStatus == cannotBeBlank)
        Assert.assertTrue(calendarForm.urlStatus == null)

        calendarForm.calendarUrl = ""
        Assert.assertEquals("", calendarForm.calendarName)
        Assert.assertEquals("", calendarForm.calendarUrl)
        Assert.assertTrue(calendarForm.nameStatus == cannotBeBlank)
        Assert.assertTrue(calendarForm.urlStatus == cannotBeBlank)

        calendarForm.calendarName = "another name"
        Assert.assertEquals("another name", calendarForm.calendarName)
        Assert.assertEquals("", calendarForm.calendarUrl)
        Assert.assertTrue(calendarForm.nameStatus == null)
        Assert.assertTrue(calendarForm.urlStatus == cannotBeBlank)

        calendarForm.calendarUrl = "http://example2.com"
        Assert.assertEquals("another name", calendarForm.calendarName)
        Assert.assertEquals("http://example2.com", calendarForm.calendarUrl)
        Assert.assertTrue(calendarForm.nameStatus == null)
        Assert.assertTrue(calendarForm.urlStatus == null)
    }

    @Test
    fun nameNotEmptyTest() {
        val name = "name"
        val url = "http://example.com"
        val calendarForm = CalendarForm(name, url)

        Assert.assertTrue(calendarForm.isNameNotEmpty())
        Assert.assertTrue(calendarForm.nameStatus == null)
    }

    @Test
    fun nameEmptyTest() {
        val invalidName = ""
        val url = "http://example.com"
        val calendarForm = CalendarForm(invalidName, url)

        Assert.assertFalse(calendarForm.isNameNotEmpty())
        Assert.assertTrue(calendarForm.nameStatus == cannotBeBlank)
    }

    @Test
    fun urlNotEmptyTest() {
        val name = "name"
        val url = "http://example.com"
        val calendarForm = CalendarForm(name, url)

        Assert.assertTrue(calendarForm.isUrlNotEmpty())
        Assert.assertTrue(calendarForm.urlStatus == null)
    }

    @Test
    fun urlEmptyTest() {
        val name = "name"
        val invalidUrl = ""
        val calendarForm = CalendarForm(name, invalidUrl)

        Assert.assertFalse(calendarForm.isUrlNotEmpty())
        Assert.assertTrue(calendarForm.urlStatus == cannotBeBlank)
    }

    @Test
    fun urlWithoutSpacesTest() {
        val name = "name"
        val url = "http://example.com"
        val calendarForm = CalendarForm(name, url)
        Assert.assertTrue(calendarForm.isUrlWithoutSpaces())
        Assert.assertTrue(calendarForm.urlStatus == null)
    }

    @Test
    fun urlWithSpacesTest() {
        val name = "name"
        val invalidUrl = "http://invalid url.com"
        val calendarForm = CalendarForm(name, invalidUrl)

        Assert.assertFalse(calendarForm.isUrlWithoutSpaces())
        Assert.assertTrue(calendarForm.urlStatus == cannotContainSpaces)
    }

    @Test
    fun urlEndedWithDotTest(){
        val name = "name"
        val invalidUrl = "http://example.com."
        val calendarForm = CalendarForm(name, invalidUrl)

        Assert.assertFalse(calendarForm.isUrlNotEndedWithDot())
        Assert.assertTrue(calendarForm.urlStatus == cannotEndWithDot)
    }

    @Test
    fun noErrorsValuesPassedByConstructorTest() {
        val calendarForm = CalendarForm("", "")
        Assert.assertTrue(calendarForm.nameStatus == null && calendarForm.urlStatus == null)
    }

    @Test
    fun urlErrorMessageOrderTest() {
        val name = "name"
        val validUrl = "http://example.com"
        val calendarForm = CalendarForm(name, validUrl)
        Assert.assertTrue(calendarForm.nameStatus == null && calendarForm.urlStatus == null)

        val withSpacesAndDot = "http://example .com."
        calendarForm.calendarUrl = withSpacesAndDot
        Assert.assertTrue(calendarForm.urlStatus == cannotContainSpaces)
    }
}