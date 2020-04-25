package io.github.maksymilianrozanski.icalreader.data

data class NumbersApiResponse(
    val text: String,
    val year: Int,
    val number: Int,
    val found: Boolean,
    val type: String
)