package io.github.maksymilianrozanski.icalreader.viewmodel

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import dagger.Component
import io.github.maksymilianrozanski.icalreader.MainActivity
import io.github.maksymilianrozanski.icalreader.MyApp
import io.github.maksymilianrozanski.icalreader.component.AppComponent
import io.github.maksymilianrozanski.icalreader.data.CalendarForm
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.model.Model
import io.github.maksymilianrozanski.icalreader.module.*
import io.reactivex.Observable
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
    fun saveNewCalendarTest() {
        val firstCalendar = WebCalendar(calendarName = "Mocked calendar first", calendarUrl = "http://example1.com")
        val insertedCalendar =
            ViewModelImpl.hardcodedCalendarToSave

        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(firstCalendar)))
        given(modelMock.saveNewCalendar(ViewModelImpl.hardcodedCalendarToSave)).willReturn(
            Observable.just(
                listOf(
                    firstCalendar,
                    insertedCalendar
                )
            )
        )

        val viewModel = ViewModelImpl(app)

        Mockito.verify(modelMock, timeout(200)).requestSavedCalendars()
        Assert.assertEquals(1, viewModel.calendars.value?.size)
        Assert.assertEquals(mutableListOf(firstCalendar), viewModel.calendars.value)

        viewModel.saveNewCalendar()

        Mockito.verify(modelMock, timeout(200)).saveNewCalendar(argThat<WebCalendar> {
            equals(ViewModelImpl.hardcodedCalendarToSave)
        })
        Assert.assertEquals(2, viewModel.calendars.value?.size)
    }

    @Test
    fun saveNewCalendarSuccessTest() {
        val savedCalendar = WebCalendar(calendarName = "Mocked calendar first", calendarUrl = "http://example1.com")
        given(modelMock.requestSavedCalendars()).willReturn(Observable.just(listOf(savedCalendar)))

        val calendarFormToSave = CalendarForm("Mocked calendar to insert", "http://example2.com")
        val calendarReturnedFromModel = CalendarForm("Returned from model", "http://example3.com")
        given(modelMock.saveNewCalendar(any<CalendarForm>())).willReturn(
            Observable.just(
                ResponseWrapper.success(
                    calendarReturnedFromModel
                )
            )
        )

        val viewModel = ViewModelImpl(app)
        viewModel.saveNewCalendar(calendarFormToSave)

        Mockito.verify(modelMock, timeout(200)).saveNewCalendar(argThat<CalendarForm> {
            equals(calendarFormToSave)
        })
        Assert.assertEquals(calendarReturnedFromModel, viewModel.calendarForm.value)
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
        Assert.assertEquals(CalendarForm.unknownError, calendarFormToSave.nameError)
    }
}