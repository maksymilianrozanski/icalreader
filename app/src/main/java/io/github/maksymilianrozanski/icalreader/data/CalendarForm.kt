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

    var nameStatus: Int? = null
    var urlStatus: Int? = null

    fun isFormValid(): Boolean {
        return isNameNotEmpty() && isUrlNotEmpty() && isUrlWithoutSpaces() && isUrlNotEndedWithDot()
    }

    fun isNameNotEmpty(): Boolean {
        return if (calendarName.isNotEmpty()) {
            nameStatus = null
            true
        } else {
            nameStatus = cannotBeBlank
            false
        }
    }

    fun isUrlNotEmpty(): Boolean {
        return if (calendarUrl.isNotEmpty()) {
            urlStatus = null
            true
        } else {
            urlStatus = cannotBeBlank
            false
        }
    }

    fun isUrlWithoutSpaces(): Boolean {
        return if (!calendarUrl.contains(" ")) {
            urlStatus = null
            true
        } else {
            urlStatus = cannotContainSpaces
            false
        }
    }

    fun isUrlNotEndedWithDot(): Boolean {
        return if (calendarUrl.endsWith(".")) {
            urlStatus = cannotEndWithDot
            false
        } else {
            urlStatus = null
            true
        }
    }

    companion object {
        const val saved = 0
        const val cannotBeBlank = 1
        const val cannotContainSpaces = 2
        const val cannotEndWithDot = 3
        const val databaseError = 4
        const val unknownError = 5
    }
}