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

        val calendar = Calendar.getInstance()
        calendar.set(2000, 1, 1, 12, 0)
        val dateStart1 = calendar.time
        calendar.set(2000, 1, 1, 13, 0)
        val dateEnd1 = calendar.time
        calendar.set(2000, 1, 1, 15, 0)
        val dateStart2 = calendar.time
        calendar.set(2000, 1, 1, 16, 30)
        val dateEnd2 = calendar.time

        val mockedEvents = listOf(
            CalendarEvent(
                title = "Informatyka Lab",
                dateStart = dateStart1,
                dateEnd = dateEnd1,
                description = "Classroom 12",
                location = "Kraków"
            ),
            CalendarEvent(
                title = "Historia",
                dateStart = dateStart2,
                dateEnd = dateEnd2,
                description = "Classroom 13",
                location = "Kraków"
            )
        )
        val iCalReader = Mockito.mock(ICalReader::class.java)
        Mockito.`when`(iCalReader.getCalendarEvents(any())).thenReturn(mockedEvents)
        val obtainedResult = arrayListOf<CalendarEvent>()

        val dataSource = Mockito.mock(EventDao::class.java)

        val model = ModelImpl(apiService, iCalReader, dataSource)
        model.requestEvents().subscribe { obtainedResult.addAll(it as Iterable<CalendarEvent>) }

        val recordedRequest = server.takeRequest()
        Assert.assertTrue(recordedRequest.method == "GET")
        Assert.assertTrue(recordedRequest.path == "/api/test.ical")

        Mockito.verify(iCalReader, timeout(200)).getCalendarEvents(argThat { equals(responseBody) })
        assertEquals(mockedEvents, obtainedResult)
    }

    @Test
    fun requestEventThrowingException() {
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

        val testObserver = model.requestEvents().test()
        testObserver.awaitTerminalEvent()
        testObserver.assertFailure(RequestFailedException::class.java)
        testObserver.assertError { t: Throwable -> t.message == "Response code: 500" }
    }

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
        calendar.set(2000, 1, 1, 12, 0)
        val dateStart1 = calendar.time
        calendar.set(2000, 1, 1, 13, 0)
        val dateEnd1 = calendar.time
        calendar.set(2000, 1, 1, 15, 0)
        val dateStart2 = calendar.time
        calendar.set(2000, 1, 1, 16, 30)
        val dateEnd2 = calendar.time

        val mockedEvents = listOf(
            CalendarEvent(
                title = "Informatyka Lab",
                dateStart = dateStart1,
                dateEnd = dateEnd1,
                description = "Classroom 12",
                location = "Kraków"
            ),
            CalendarEvent(
                title = "Historia",
                dateStart = dateStart2,
                dateEnd = dateEnd2,
                description = "Classroom 13",
                location = "Kraków"
            )
        )
        val iCalReader = Mockito.mock(ICalReader::class.java)
        Mockito.`when`(iCalReader.getCalendarEvents(any())).thenReturn(mockedEvents)
        val dataSource = Mockito.mock(EventDao::class.java)
        val model = ModelImpl(apiService, iCalReader, dataSource)

        model.requestCalendarResponse().test().await().assertNoErrors().assertValue {
            it.status == "Success" && it.data == mockedEvents
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

        model.requestCalendarResponse().test().await().assertNoErrors().assertValue {
            it.status == "Error" && it.message == "500"
        }

        val recordedRequest = server.takeRequest()
        Assert.assertTrue(recordedRequest.method == "GET")
        Assert.assertTrue(recordedRequest.path == "/api/test.ical")
    }
}