package io.github.maksymilianrozanski.icalreader.model

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.timeout
import io.github.maksymilianrozanski.icalreader.TestHelper
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.module.NetworkTestModule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

class ModelImplTest {

    val testHelper = TestHelper()

    @Test
    fun requestEvents() {
        val server = MockWebServer()
        server.start()
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        val responseBody = testHelper.getStringFromFile("exampleCalendar.txt")
        mockResponse.setBody(responseBody)
        server.enqueue(mockResponse)

        val networkModule = NetworkTestModule(server.url("/").toString())
        val apiService = networkModule.provideApi()

        val mockedEvents = listOf(
            CalendarEvent(
                "Informatyka Lab",
                "01 Jan 2000 12:00",
                "01 Jan 2000 13:00",
                "Classroom 12",
                "Kraków"
            ),
            CalendarEvent(
                "Historia",
                "01 Jan 2000 15:00",
                "01 Jan 2000 16:30",
                "Classroom 13",
                "Kraków"
            )
        )
        val iCalReader = Mockito.mock(ICalReader::class.java)
        Mockito.`when`(iCalReader.getCalendarEvents(any())).thenReturn(mockedEvents)
        val obtainedResult = arrayListOf<CalendarEvent>()

        val model = ModelImpl(apiService, iCalReader)
        model.requestEvents().subscribe { obtainedResult.addAll(it as Iterable<CalendarEvent>) }

        val recordedRequest = server.takeRequest()
        Assert.assertTrue(recordedRequest.method == "GET")
        Assert.assertTrue(recordedRequest.path == "/api/test.ical")

        Mockito.verify(iCalReader, timeout(200)).getCalendarEvents(argThat { equals(responseBody) })
        assertEquals(mockedEvents, obtainedResult)
    }
}