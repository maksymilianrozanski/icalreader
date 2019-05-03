package io.github.maksymilianrozanski.icalreader.model

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.argWhere
import com.nhaarman.mockitokotlin2.timeout
import io.github.maksymilianrozanski.icalreader.TestHelper
import io.github.maksymilianrozanski.icalreader.data.APIService
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.CalendarForm
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.model.storage.EventDao
import io.github.maksymilianrozanski.icalreader.module.NetworkTestModule
import io.reactivex.Completable
import io.reactivex.subjects.SingleSubject
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

    @Test
    fun requestResponseDynamicUrlTest() {
        val server = MockWebServer()
        server.start()

        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        val responseBody = testHelper.getStringFromFile("exampleCalendar.txt")
        mockResponse.setBody(responseBody)
        server.enqueue(mockResponse)

        val networkModule = NetworkTestModule("http://notUsedBaseUrl/")
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

        val serverBaseUrl = server.url("/").toString()
        val urlPath = "expected/url"
        val urlExpectedInRequest = "$serverBaseUrl$urlPath"

        val webCalendar =
            WebCalendar(calendarName = "example calendar name", calendarUrl = urlExpectedInRequest)

        model.requestCalendarResponseFromApi(webCalendar).test().await().assertNoErrors().assertValue {
            it.status == "Success"
                    //should be sorted by date start from oldest (eg. 1999) to most recent (eg. 2019)
                    && it.data.events[0] == mockedEvents[1] && it.data.events[1] == mockedEvents[0] && it.data.events[2] == mockedEvents[2]
                    && it.data.webCalendar == webCalendar
        }

        val recordedRequest = server.takeRequest()
        Assert.assertTrue(recordedRequest.method == "GET")
        Assert.assertTrue(recordedRequest.requestUrl.toString() == urlExpectedInRequest)

        Mockito.verify(iCalReader, timeout(200)).getCalendarEvents(argThat { equals(responseBody) })
    }

    @Test
    fun requestResponseDynamicUrlErrorTest() {
        val server = MockWebServer()
        server.start()
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(500)
        mockResponse.setBody("")
        server.enqueue(mockResponse)

        val networkModule = NetworkTestModule("http://notUsedBaseUrl/")
        val apiService = networkModule.provideApi()
        val iCalReader = Mockito.mock(ICalReader::class.java)
        val dataSource = Mockito.mock(EventDao::class.java)

        val model = ModelImpl(apiService, iCalReader, dataSource)

        val serverBaseUrl = server.url("/").toString()
        val urlPath = "expected/url"
        val urlExpectedInRequest = "$serverBaseUrl$urlPath"

        val webCalendar =
            WebCalendar(calendarName = "example calendar name", calendarUrl = urlExpectedInRequest)

        model.requestCalendarResponseFromApi(webCalendar).test().await().assertNoErrors().assertValue {
            it.status == "Error" && it.message == "500"
        }

        val recordedRequest = server.takeRequest()
        Assert.assertTrue(recordedRequest.method == "GET")
        Assert.assertTrue(recordedRequest.requestUrl.toString() == urlExpectedInRequest)
    }

    @Test
    fun replaceSavedEventsTest() {
        val apiService = Mockito.mock(APIService::class.java)
        val iCalReader = Mockito.mock(ICalReader::class.java)
        val dataSource = Mockito.mock(EventDao::class.java)
        val model = ModelImpl(apiService, iCalReader, dataSource)

        val eventFromApiOne = CalendarEvent(
            calendarId = "Should be updated",
            title = "example title",
            dateStart = Date(939543010000L),
            dateEnd = Date(939550210000L),
            description = "example description",
            location = "example location"
        )

        val eventFromApiTwo = CalendarEvent(
            calendarId = "Should be updated",
            title = "example title two",
            dateStart = Date(971172610000L),
            dateEnd = Date(971183410000L),
            description = "example description two",
            location = "example location two"
        )

        val webCalendar = WebCalendar(calendarName = "Calendar name", calendarUrl = "http://example.com")

        val eventsFromApi = listOf(eventFromApiOne, eventFromApiTwo)

        model.replaceSavedEvents(webCalendar, eventsFromApi)

        Mockito.verify(dataSource).deleteAllEventsOfCalendar(argThat { equals(webCalendar.calendarId) })
        Mockito.verify(dataSource)
            .insertEventsList(argWhere {
                it[0].calendarId == webCalendar.calendarId
                        && it[0].title == eventFromApiOne.title
                        && it[0].dateStart == eventFromApiOne.dateStart
                        && it[0].dateEnd == eventFromApiOne.dateEnd
                        && it[0].description == eventFromApiOne.description
                        && it[0].location == eventFromApiOne.location
                        && it[1].calendarId == webCalendar.calendarId
                        && it[1].title == eventFromApiTwo.title
                        && it[1].dateStart == eventFromApiTwo.dateStart
                        && it[1].dateEnd == eventFromApiTwo.dateEnd
                        && it[1].description == eventFromApiTwo.description
                        && it[1].location == eventFromApiTwo.location
            })
    }

    @Test
    fun savingNewCalendarFormTest() {
        val apiService = Mockito.mock(APIService::class.java)
        val iCalReader = Mockito.mock(ICalReader::class.java)
        val dataSource = Mockito.mock(EventDao::class.java)
        val model = ModelImpl(apiService, iCalReader, dataSource)

        val calendarForm = CalendarForm(calendarName = "Example name", calendarUrl = "http://example.com")

        Mockito.`when`(dataSource.insertCalendarSingle(argThat {
            calendarName == "Example name" && calendarUrl == "http://example.com"
        })).thenReturn(Completable.complete())

        model.saveNewCalendar(calendarForm).test().await().assertNoErrors().assertValue {
            it.status == "Success" && it.data == calendarForm
        }
    }

    @Test
    fun savingNewCalendarFormDbErrorTest() {
        val apiService = Mockito.mock(APIService::class.java)
        val iCalReader = Mockito.mock(ICalReader::class.java)
        val dataSource = Mockito.mock(EventDao::class.java)
        val model = ModelImpl(apiService, iCalReader, dataSource)

        val calendarForm = CalendarForm(calendarName = "Example name", calendarUrl = "http://example.com")

        Mockito.`when`(dataSource.insertCalendarSingle(argThat {
            calendarName == "Example name" && calendarUrl == "http://example.com"
        })).thenReturn(Completable.error(Throwable("Database error")))

        model.saveNewCalendar(calendarForm).test().await().assertNoErrors().assertValue {
            it.status == "Error" && it.data == calendarForm && it.message == "Database error"
                    && it.data.nameError == CalendarForm.databaseError
        }
    }

    @Test
    fun savingNewCalendarFormInvalidUrlTest() {
        val apiService = Mockito.mock(APIService::class.java)
        val iCalReader = Mockito.mock(ICalReader::class.java)
        val dataSource = Mockito.mock(EventDao::class.java)
        val model = ModelImpl(apiService, iCalReader, dataSource)

        val calendarForm = CalendarForm("", "")
        calendarForm.calendarName = "name"
        calendarForm.calendarUrl = "http://invalid url."

        model.saveNewCalendar(calendarForm).test().await().assertValue {
            it.status == "Error" && it.data.calendarName == "name" && it.data.calendarUrl == "http://invalid url."
                    && it.data.nameError == null && it.data.urlError == CalendarForm.cannotContainSpaces
                    && it.message == "Invalid input"
        }
    }

    @Test
    fun requestSavedDataOfWebCalendarSuccessTest() {
        val apiService = Mockito.mock(APIService::class.java)
        val iCalReader = Mockito.mock(ICalReader::class.java)
        val dataSource = Mockito.mock(EventDao::class.java)
        val model = ModelImpl(apiService, iCalReader, dataSource)

        val webCalendar = WebCalendar(calendarName = "Example name", calendarUrl = "http://example.com")
        val event = CalendarEvent(
            calendarId = webCalendar.calendarId,
            title = "Some title",
            dateStart = Date(1544605810000L),
            dateEnd = Date(1544692210000L),
            description = "description",
            location = "location"
        )
        val subject = SingleSubject.create<List<CalendarEvent>>()
        Mockito.`when`(dataSource.getEventsOfCalendarSingle(webCalendar.calendarId)).thenReturn(subject)
        subject.onSuccess(listOf(event))

        model.requestSavedData(webCalendar).test().assertNoErrors().await().assertValue {
            it.status == "Success" && it.data.webCalendar == webCalendar && it.data.events == listOf(event)
        }
    }

    @Test
    fun requestSavedDataOfWebCalendarEmptyEventsTest() {
        val apiService = Mockito.mock(APIService::class.java)
        val iCalReader = Mockito.mock(ICalReader::class.java)
        val dataSource = Mockito.mock(EventDao::class.java)
        val model = ModelImpl(apiService, iCalReader, dataSource)

        val webCalendar = WebCalendar(calendarName = "Example name", calendarUrl = "http://example.com")

        val subject = SingleSubject.create<List<CalendarEvent>>()
        Mockito.`when`(dataSource.getEventsOfCalendarSingle(webCalendar.calendarId)).thenReturn(subject)
        subject.onSuccess(listOf())

        model.requestSavedData(webCalendar).test().assertNoErrors().await().assertValue {
            it.status == "Success" && it.data.webCalendar == webCalendar
                    && it.data.events == listOf<CalendarEvent>()
        }
    }
}