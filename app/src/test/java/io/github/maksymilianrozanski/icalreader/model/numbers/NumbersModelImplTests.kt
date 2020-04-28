package io.github.maksymilianrozanski.icalreader.model.numbers

import arrow.core.Either
import arrow.core.getOrElse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody


import org.junit.Assert
import org.junit.Test
import retrofit2.Response
import kotlin.test.fail

class NumbersModelImplTests {
    @Test
    fun shouldReturnResponse() {
        val responseBody = Response.success("Hello")
        val result =
            NumbersModelImpl.isSuccessful(responseBody).getOrElse { fail("Should return response") }
        Assert.assertEquals(responseBody, result)
    }

    @Test
    fun shouldReturnError() {
        val code = 404
        val errorResponse =
            Response.error<String>(
                code,
                "error message body".toResponseBody("text/plain".toMediaTypeOrNull())
            )

        val expected = NumbersApiError(code)

        val result = when (val result = NumbersModelImpl.isSuccessful(errorResponse)) {
            is Either.Left -> result.a
            else -> fail("Should match to the left")
        }

        Assert.assertEquals(expected, result)
    }
}