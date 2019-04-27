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

    var nameError: Int? = null
    var urlError: Int? = null

    fun isFormValid(): Boolean {
        return isNameNotEmpty() && isUrlNotEmpty() && isUrlWithoutSpaces() && isUrlNotEndedWithDot()
    }

    fun isNameNotEmpty(): Boolean {
        return if (calendarName.isNotEmpty()) {
            nameError = null
            true
        } else {
            nameError = cannotBeBlank
            false
        }
    }

    fun isUrlNotEmpty(): Boolean {
        return if (calendarUrl.isNotEmpty()) {
            urlError = null
            true
        } else {
            urlError = cannotBeBlank
            false
        }
    }

    fun isUrlWithoutSpaces(): Boolean {
        return if (!calendarUrl.contains(" ")) {
            urlError = null
            true
        } else {
            urlError = cannotContainSpaces
            false
        }
    }

    fun isUrlNotEndedWithDot(): Boolean {
        return if (calendarUrl.endsWith(".")) {
            urlError = cannotEndWithDot
            false
        } else {
            urlError = null
            true
        }
    }

    companion object {
        const val cannotBeBlank = 1
        const val cannotContainSpaces = 2
        const val cannotEndWithDot = 3
        const val databaseError = 4
    }
}