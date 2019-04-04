package io.github.maksymilianrozanski.icalreader.data

data class CalendarResponse<out T>(val status: String, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T?): CalendarResponse<T> = CalendarResponse("Success", data, null)
        fun <T> error(data: T?, message: String?): CalendarResponse<T> = CalendarResponse("Error", data, message)
        fun <T> loading(data: T?): CalendarResponse<T> = CalendarResponse("Loading", data, null)
    }
}

