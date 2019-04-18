package io.github.maksymilianrozanski.icalreader.model

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.timeout
import io.github.maksymilianrozanski.icalreader.TestHelper
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.model.storage.EventDao
import io.github.maksymilianrozanski.icalreader.module.NetworkTestModule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.util.*

class ModelImplTest {

    val testHelper = TestHelper()

    @Test
    fun requestCalendarResponseSuccessTest() {
        val server = MockWebServer()
        server.start()
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        val responseBody = testHelper.getStringFromFile("exampleCalendar.txt")
        mockResponse.setBody(responseBody)
        server.enqueue(mockResponse)

        val networkModule = NetworkTestModule(server.url("/").toString())
        val apiService = networkModule.provideApi()

        val calendar = Calendar.getInstance()
        calendar.set(2000, 1, 1, 15, 0)
        val newestEventDateStart = calendar.time
        calendar.set(2000, 1, 1, 16, 30)
        val newestEventDateEnd = calendar.time
        calendar.set(2000, 1, 1, 12, 0)
        val middleEventDateStart = calendar.time
        calendar.set(2000, 1, 1, 13, 0)
        val middleEventDateEnd = calendar.time
        calendar.set(1999, 2, 2, 12, 20)
        val oldestEventDateStart = calendar.time
        calendar.set(1999, 2, 2, 14, 40)
        val oldestEventDateEnd = calendar.time

        val mockedEvents = listOf(
            CalendarEvent(
                calendarId = "12345",
                title = "Informatyka Lab",
                dateStart = middleEventDateStart,
                dateEnd = middleEventDateEnd,
                description = "Classroom 12",
                location = "Kraków"
            ),
            CalendarEvent(
                calendarId = "12345",
                title = "Chemia",
                dateStart = oldestEventDateStart,
                dateEnd = oldestEventDateEnd,
                description = "Classroom14",
                location = "Kraków"
            ),
            CalendarEvent(
                calendarId = "12345",
                title = "Historia",
                dateStart = newestEventDateStart,
                dateEnd = newestEventDateEnd,
                description = "Classroom 13",
                location = "Kraków"
            )
        )
        val iCalReader = Mockito.mock(ICalReader::class.java)
        Mockito.`when`(iCalReader.getCalendarEvents(any())).thenReturn(mockedEvents)
        val dataSource = Mockito.mock(EventDao::class.java)
        val model = ModelImpl(apiService, iCalReader, dataSource)

        model.requestCalendarResponseFromApi().test().await().assertNoErrors().assertValue {
            it.status == "Success"
                    //should be sorted by date start from oldest (eg. 1999) to most recent (eg. 2019)
                    && it.data[0] == mockedEvents[1] && it.data[1] == mockedEvents[0] && it.data[2] == mockedEvents[2]
        }

        val recordedRequest = server.takeRequest()
        Assert.assertTrue(recordedRequest.method == "GET")
        Assert.assertTrue(recordedRequest.path == "/api/test.ical")

        Mockito.verify(iCalReader, timeout(200)).getCalendarEvents(argThat { equals(responseBody) })
    }

    @Test
    fun requestCalendarResponseErrorTest() {
        val server = MockWebServer()
        server.start()
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(500)
        mockResponse.setBody("")
        server.enqueue(mockResponse)

        val networkModule = NetworkTestModule(server.url("/").toString())
        val apiService = networkModule.provideApi()
        val iCalReader = Mockito.mock(ICalReader::class.java)
        val dataSource = Mockito.mock(EventDao::class.java)

        val model = ModelImpl(apiService, iCalReader, dataSource)

        model.requestCalendarResponseFromApi().test().await().assertNoErrors().assertValue {
            it.status == "Error" && it.message == "500"
        }

        val recordedRequest = server.takeRequest()
        Assert.assertTrue(recordedRequest.method == "GET")
        Assert.assertTrue(recordedRequest.path == "/api/test.ical")
    }
}