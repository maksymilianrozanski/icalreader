package io.github.maksymilianrozanski.icalreader.data

class CalendarForm(calendarName: String, calendarUrl: String) {

    var calendarName:String = calendarName
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                nameError = null
            } else {
                nameError = "Cannot be blank"
            }
        }

    var calendarUrl = calendarUrl
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                urlError = null
            } else {
                urlError = "Cannot be blank"
            }
        }

    var nameError: String? = null
    var urlError: String? = null
}