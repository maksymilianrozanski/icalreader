package io.github.maksymilianrozanski.icalreader.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.times
import dagger.Component
import io.github.maksymilianrozanski.icalreader.MainActivity
import io.github.maksymilianrozanski.icalreader.MyApp
import io.github.maksymilianrozanski.icalreader.component.AppComponent
import io.github.maksymilianrozanski.icalreader.data.*
import io.github.maksymilianrozanski.icalreader.model.Model
import io.github.maksymilianrozanski.icalreader.module.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.timeout
import java.util.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class, ViewModelModule::class, ModelImplTestModule::class,
        CalendarTestModule::class, TrampolineSchedulerProviderTestModule::class]
)
interface ViewModelTestAppComponent : AppComponent {
    fun inject(test: ViewModelImplTest)
}

class ViewModelImplTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var app: MyApp
    private lateinit var modelMock: Model

    @Before
    fun setUp() {
        app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MyApp
        modelMock = Mockito.mock(Model::class.java)
        val testAppComponent: ViewModelTestAppComponent = DaggerViewModelTestAppComponent.builder()
            .appModule(AppModule(app))
            .modelImplTestModule(ModelImplTestModule(modelMock))
            .calendarTestModule(CalendarTestModule(Mockito.mock(Calendar::class.java)))
            .trampolineSchedulerProviderTestModule(TrampolineSchedulerProviderTestModule())
            .build()

        app.appComponent = testAppComponent
        testAppComponent.inject(this)
    }

    @Test
    fun saveNewCalendarSuccessTest() {
        val savedCalendar = WebCalendar(calendarName = "Mocked calendar first", calendarUrl = "http://example1.com")
        val calendarToSave =
            WebCalendar(calendarName = "Mocked calendar to insert", calendarUrl = "http://example2.com")
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(savedCalendar)))

        val calendarFormToSave = CalendarForm("Mocked calendar to insert", "http://example2.com")
        val calendarReturnedFromModel = CalendarForm("Mocked calendar to insert", "http://example2.com")
        given(modelMock.saveNewCalendar(any<CalendarForm>())).willReturn(
            Observable.just(
                ResponseWrapper.success(
                    calendarReturnedFromModel
                )
            )
        )

        val viewModel = ViewModelImpl(app)
        Assert.assertEquals(listOf(savedCalendar), viewModel.calendars.value!!)

        given(modelMock.requestSavedCalendars()).willReturn(
            Observable.just(listOf(savedCalendar, calendarToSave))
        )
        val calendarToSaveEvents = mutableListOf(
            CalendarEvent(
                calendarId = calendarToSave.calendarId,
                title = "Some event",
                dateStart = Date(1544612400000L),
                dateEnd = Date(1544626800000L),
                location = "Example location",
                description = "Example description"
            )
        )
        given(modelMock.requestCalendarData(calendarToSave)).willReturn(
            Observable.just(
                ResponseWrapper.success(
                    CalendarData(
                        calendarToSave,
                        calendarToSaveEvents
                    )
                )
            )
        )
        viewModel.saveNewCalendar(calendarFormToSave)

        Mockito.verify(
            modelMock, timeout(200)
        ).saveNewCalendar(argThat<CalendarForm> {
            equals(calendarFormToSave)
        })
        Mockito.verify(modelMock, timeout(200)).requestCalendarData(calendarToSave)

        Assert.assertTrue(viewModel.eventsData.value!!.data == CalendarData(calendarToSave, calendarToSaveEvents))
        Assert.assertTrue(
            calendarReturnedFromModel.calendarName == viewModel.calendarForm.value!!.calendarName
                    && calendarReturnedFromModel.calendarUrl == viewModel.calendarForm.value!!.calendarUrl
                    && calendarReturnedFromModel.nameStatus == viewModel.calendarForm.value!!.nameStatus
                    && calendarReturnedFromModel.urlStatus == viewModel.calendarForm.value!!.urlStatus
        )
        Assert.assertEquals(listOf(savedCalendar, calendarToSave), viewModel.calendars.value!!)
    }

    @Test
    fun savingNewCalendarErrorThrownByModelTest() {
        val savedCalendar = WebCalendar(calendarName = "Mocked calendar first", calendarUrl = "http://example1.com")
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(savedCalendar)))

        val calendarFormToSave = CalendarForm("Mocked calendar to insert", "http://example2.com")
        given(modelMock.saveNewCalendar(any<CalendarForm>())).willReturn(Observable.error(Throwable("Some error")))

        val viewModel = ViewModelImpl(app)
        viewModel.saveNewCalendar(calendarFormToSave)

        Mockito.verify(modelMock, timeout(200)).saveNewCalendar(argThat<CalendarForm> {
            equals(calendarFormToSave)
        })
        Assert.assertEquals(CalendarForm.unknownError, calendarFormToSave.nameStatus)
    }

    @Test
    fun requestCalendarResponseSuccessTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        val event1 = CalendarEvent(
            calendarId = secondCalendar.calendarId, title = "First title",
            dateStart = Date(1328209200000L), dateEnd = Date(1328216400000), description = "Description",
            location = "Location"
        )
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()

        given(modelMock.requestCalendarData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.requestCalendarResponse(secondCalendar)

        Mockito.verify(modelMock).requestCalendarData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf())), viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event1))))
        Assert.assertEquals(
            ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event1))),
            viewModel.eventsData.value
        )
    }

    @Test
    fun requestSavedCalendarDataSuccessTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        val event1 = CalendarEvent(
            calendarId = secondCalendar.calendarId, title = "First title",
            dateStart = Date(1328209200000L), dateEnd = Date(1328216400000), description = "Description",
            location = "Location"
        )
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()

        given(modelMock.requestSavedData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.requestSavedCalendarData(secondCalendar)

        Mockito.verify(modelMock).requestSavedData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf())), viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event1))))
        Assert.assertEquals(
            ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event1))),
            viewModel.eventsData.value
        )
    }

    @Test
    fun requestCalendarResponseErrorTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()
        given(modelMock.requestCalendarData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.requestCalendarResponse(secondCalendar)

        Mockito.verify(modelMock).requestCalendarData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf())), viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf()), "500"))
        Assert.assertEquals(
            ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf()), "500"),
            viewModel.eventsData.value
        )
    }

    @Test
    fun requestSavedCalendarDataErrorTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()
        given(modelMock.requestSavedData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.requestSavedCalendarData(secondCalendar)

        Mockito.verify(modelMock).requestSavedData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf())), viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf()), "500"))
        Assert.assertEquals(
            ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf()), "500"),
            viewModel.eventsData.value
        )
    }

    @Test
    fun refreshingDataNoSuccessTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        val event1 = CalendarEvent(
            calendarId = secondCalendar.calendarId, title = "First title",
            dateStart = Date(1328209200000L), dateEnd = Date(1328216400000), description = "Description",
            location = "Location"
        )
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()
        given(modelMock.requestCalendarData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.eventsData.value = ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event1)))
        viewModel.requestCalendarResponse(secondCalendar)

        Mockito.verify(modelMock).requestCalendarData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf(event1))),
            viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf()), "500"))
        //Should display old data with error status
        Assert.assertEquals(
            ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf(event1)), "500"),
            viewModel.eventsData.value
        )
    }

    @Test
    fun requestSavedCalendarDataNoSuccessTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        val event1 = CalendarEvent(
            calendarId = secondCalendar.calendarId, title = "First title",
            dateStart = Date(1328209200000L), dateEnd = Date(1328216400000), description = "Description",
            location = "Location"
        )
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()
        given(modelMock.requestSavedData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.eventsData.value = ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event1)))
        viewModel.requestSavedCalendarData(secondCalendar)

        Mockito.verify(modelMock).requestSavedData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf(event1))),
            viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf()), "500"))
        //Should display old data with error status
        Assert.assertEquals(
            ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf(event1)), "500"),
            viewModel.eventsData.value
        )
    }

    @Test
    fun refreshingDataErrorWithDataTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        val event1 = CalendarEvent(
            calendarId = secondCalendar.calendarId, title = "First title",
            dateStart = Date(1328209200000L), dateEnd = Date(1328216400000), description = "Description",
            location = "Location"
        )
        val event2 = CalendarEvent(
            calendarId = secondCalendar.calendarId, title = "Second title",
            dateStart = Date(1544612400000L), dateEnd = Date(1544626800000L),
            description = "Description2", location = "Location2"
        )

        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()
        given(modelMock.requestCalendarData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.eventsData.value = ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event1)))
        viewModel.requestCalendarResponse(secondCalendar)

        Mockito.verify(modelMock).requestCalendarData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf(event1))),
            viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf(event1, event2)), "500"))
        Assert.assertEquals(
            ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf(event1, event2)), "500"),
            viewModel.eventsData.value
        )
    }

    @Test
    fun refreshingDataSuccessTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        val event1 = CalendarEvent(
            calendarId = secondCalendar.calendarId, title = "First title",
            dateStart = Date(1328209200000L), dateEnd = Date(1328216400000), description = "Description",
            location = "Location"
        )
        val event2 = CalendarEvent(
            calendarId = secondCalendar.calendarId, title = "Second title",
            dateStart = Date(1544612400000L), dateEnd = Date(1544626800000L),
            description = "Description2", location = "Location2"
        )
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()
        given(modelMock.requestCalendarData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.eventsData.value = ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event1)))
        viewModel.requestCalendarResponse(secondCalendar)

        Mockito.verify(modelMock).requestCalendarData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf(event1))),
            viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event1, event2))))
        Assert.assertEquals(
            ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event1, event2))),
            viewModel.eventsData.value
        )
    }

    @Test
    fun displayingOtherCalendarSuccessTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        val event1 = CalendarEvent(
            calendarId = firstCalendar.calendarId, title = "First title",
            dateStart = Date(1328209200000L), dateEnd = Date(1328216400000), description = "Description",
            location = "Location"
        )
        val event2 = CalendarEvent(
            calendarId = secondCalendar.calendarId, title = "Second title",
            dateStart = Date(1544612400000L), dateEnd = Date(1544626800000L),
            description = "Description2", location = "Location2"
        )
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()
        given(modelMock.requestCalendarData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.eventsData.value = ResponseWrapper.success(CalendarData(firstCalendar, mutableListOf(event1)))
        viewModel.requestCalendarResponse(secondCalendar)

        Mockito.verify(modelMock).requestCalendarData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf())),
            viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event2))))
        Assert.assertEquals(
            ResponseWrapper.success(CalendarData(secondCalendar, mutableListOf(event2))),
            viewModel.eventsData.value
        )
    }

    @Test
    fun displayingOtherCalendarErrorWithoutDataTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        val event1 = CalendarEvent(
            calendarId = firstCalendar.calendarId, title = "First title",
            dateStart = Date(1328209200000L), dateEnd = Date(1328216400000), description = "Description",
            location = "Location"
        )

        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()
        given(modelMock.requestCalendarData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.eventsData.value = ResponseWrapper.success(CalendarData(firstCalendar, mutableListOf(event1)))
        viewModel.requestCalendarResponse(secondCalendar)

        Mockito.verify(modelMock).requestCalendarData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf())),
            viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf()), "500"))
        Assert.assertEquals(
            ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf()), "500"),
            viewModel.eventsData.value
        )
    }

    @Test
    fun displayingOtherCalendarErrorWithDataTest() {
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        val event1 = CalendarEvent(
            calendarId = firstCalendar.calendarId, title = "First title",
            dateStart = Date(1328209200000L), dateEnd = Date(1328216400000), description = "Description",
            location = "Location"
        )
        val event2 = CalendarEvent(
            calendarId = secondCalendar.calendarId, title = "Second title",
            dateStart = Date(1544612400000L), dateEnd = Date(1544626800000L),
            description = "Description2", location = "Location2"
        )
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val publishSubject: PublishSubject<ResponseWrapper<CalendarData>> = PublishSubject.create()
        given(modelMock.requestCalendarData(secondCalendar)).willReturn(publishSubject)

        val viewModel = ViewModelImpl(app)
        viewModel.eventsData.value = ResponseWrapper.success(CalendarData(firstCalendar, mutableListOf(event1)))
        viewModel.requestCalendarResponse(secondCalendar)

        Mockito.verify(modelMock).requestCalendarData(argThat { equals(secondCalendar) })
        Assert.assertEquals(
            ResponseWrapper.loading(CalendarData(secondCalendar, mutableListOf())),
            viewModel.eventsData.value
        )

        publishSubject.onNext(ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf(event2)), "500"))
        Assert.assertEquals(
            ResponseWrapper.error(CalendarData(secondCalendar, mutableListOf(event2)), "500"),
            viewModel.eventsData.value
        )
    }

    @Test
    fun deleteCalendarTest(){
        val firstCalendar = WebCalendar(calendarName = "First calendar", calendarUrl = "http://example1.com")
        val secondCalendar = WebCalendar(calendarName = "Second calendar", calendarUrl = "http://example2.com")
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar, secondCalendar)))

        val viewModel = ViewModelImpl(app)

        given(modelMock.deleteCalendar(firstCalendar)).willReturn(Completable.complete())
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(secondCalendar)))
        viewModel.deleteCalendar(firstCalendar)

        Mockito.verify(modelMock).deleteCalendar(argThat { equals(firstCalendar) })
        Mockito.verify(modelMock, times(2)).requestSavedCalendars()

        Assert.assertEquals(listOf(secondCalendar), viewModel.calendars.value)
    }
}