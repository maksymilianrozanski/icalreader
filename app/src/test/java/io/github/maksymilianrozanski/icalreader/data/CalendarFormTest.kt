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

    @Test
    fun nameNotEmptyTest() {
        val name = "name"
        val url = "http://example.com"
        val calendarForm = CalendarForm(name, url)

        Assert.assertTrue(calendarForm.isNameNotEmpty())
        Assert.assertTrue(calendarForm.nameError == null)
    }

    @Test
    fun nameEmptyTest() {
        val invalidName = ""
        val url = "http://example.com"
        val calendarForm = CalendarForm(invalidName, url)

        Assert.assertFalse(calendarForm.isNameNotEmpty())
        Assert.assertTrue(calendarForm.nameError == "Cannot be blank")
    }

    @Test
    fun urlNotEmptyTest() {
        val name = "name"
        val url = "http://example.com"
        val calendarForm = CalendarForm(name, url)

        Assert.assertTrue(calendarForm.isUrlNotEmpty())
        Assert.assertTrue(calendarForm.urlError == null)
    }

    @Test
    fun urlEmptyTest() {
        val name = "name"
        val invalidUrl = ""
        val calendarForm = CalendarForm(name, invalidUrl)

        Assert.assertFalse(calendarForm.isUrlNotEmpty())
        Assert.assertTrue(calendarForm.urlError == "Cannot be blank")
    }

    @Test
    fun urlWithoutSpacesTest() {
        val name = "name"
        val url = "http://example.com"
        val calendarForm = CalendarForm(name, url)
        Assert.assertTrue(calendarForm.isUrlWithoutSpaces())
        Assert.assertTrue(calendarForm.urlError == null)
    }

    @Test
    fun urlWithSpacesTest() {
        val name = "name"
        val invalidUrl = "http://invalid url.com"
        val calendarForm = CalendarForm(name, invalidUrl)

        Assert.assertFalse(calendarForm.isUrlWithoutSpaces())
        Assert.assertTrue(calendarForm.urlError == "Cannot contain spaces")
    }

    @Test
    fun urlEndedWithDotTest(){
        val name = "name"
        val invalidUrl = "http://example.com."
        val calendarForm = CalendarForm(name, invalidUrl)

        Assert.assertFalse(calendarForm.isUrlNotEndedWithDot())
        Assert.assertTrue(calendarForm.urlError == "Cannot end with '.'")
    }

    @Test
    fun noErrorsValuesPassedByConstructorTest() {
        val calendarForm = CalendarForm("", "")
        Assert.assertTrue(calendarForm.nameError == null && calendarForm.urlError == null)
    }

    @Test
    fun urlErrorMessageOrderTest() {
        val name = "name"
        val validUrl = "http://example.com"
        val calendarForm = CalendarForm(name, validUrl)
        Assert.assertTrue(calendarForm.nameError == null && calendarForm.urlError == null)

        val withSpacesAndDot = "http://example .com."
        calendarForm.calendarUrl = withSpacesAndDot
        Assert.assertTrue(calendarForm.urlError == "Cannot contain spaces")
    }
}