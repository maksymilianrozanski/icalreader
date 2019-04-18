package io.github.maksymilianrozanski.icalreader.data

data class ResponseWrapper<out T>(val status: String, val data: T, val message: String?) {

    companion object {
        fun <T> success(data: T): ResponseWrapper<T> = ResponseWrapper("Success", data, null)
        fun <T> error(data: T, message: String?): ResponseWrapper<T> = ResponseWrapper("Error", data, message)
        fun <T> loading(data: T): ResponseWrapper<T> = ResponseWrapper("Loading", data, null)
    }
}

