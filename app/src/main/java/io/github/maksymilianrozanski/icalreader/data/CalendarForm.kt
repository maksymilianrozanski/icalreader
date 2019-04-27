package io.github.maksymilianrozanski.icalreader.data

class CalendarForm(calendarName: String, calendarUrl: String) {

    var calendarName: String = calendarName
        set(value) {
            field = value
            isNameNotEmpty()
        }

    var calendarUrl = calendarUrl
        set(value) {
            field = value
            //first error from the left is set
            isUrlNotEmpty() && isUrlWithoutSpaces() && isUrlNotEndedWithDot()
        }

    var nameError: String? = null
    var urlError: String? = null

    fun isNameNotEmpty(): Boolean {
        return if (calendarName.isNotEmpty()) {
            nameError = null
            true
        } else {
            nameError = "Cannot be blank"
            false
        }
    }

    fun isUrlNotEmpty(): Boolean {
        return if (calendarUrl.isNotEmpty()) {
            urlError = null
            true
        } else {
            urlError = "Cannot be blank"
            false
        }
    }

    fun isUrlWithoutSpaces(): Boolean {
        return if (!calendarUrl.contains(" ")) {
            urlError = null
            true
        } else {
            urlError = "Cannot contain spaces"
            false
        }
    }

    fun isUrlNotEndedWithDot():Boolean {
        return if (calendarUrl.endsWith(".")) {
            urlError = "Cannot end with '.'"
            false
        } else {
            urlError = null
            true
        }
    }
}