package io.github.maksymilianrozanski.icalreader.viewmodel

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.nhaarman.mockitokotlin2.argThat
import dagger.Component
import io.github.maksymilianrozanski.icalreader.MainActivity
import io.github.maksymilianrozanski.icalreader.MyApp
import io.github.maksymilianrozanski.icalreader.component.AppComponent
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.model.Model
import io.github.maksymilianrozanski.icalreader.module.*
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
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
@Component(modules = [AppModule::class, ViewModelModule::class, ModelImplTestModule::class, CalendarTestModule::class, SchedulerProviderTestModule::class])
interface ViewModelTestAppComponent : AppComponent {
    fun inject(test: ViewModelImplTest)
}

class ViewModelImplTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    private lateinit var app: MyApp
    private lateinit var modelMock: Model
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        testScheduler = TestScheduler()

        app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MyApp
        modelMock = Mockito.mock(Model::class.java)
        val testAppComponent = DaggerViewModelTestAppComponent.builder()
            .appModule(AppModule(app))
            .modelImplTestModule(ModelImplTestModule(modelMock))
            .calendarTestModule(CalendarTestModule(Mockito.mock(Calendar::class.java)))
            .schedulerProviderTestModule(SchedulerProviderTestModule(testScheduler))
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
        testScheduler.triggerActions()

        Mockito.verify(modelMock, timeout(200)).requestSavedCalendars()
        Assert.assertEquals(1, viewModel.calendars.value?.size)
        Assert.assertEquals(mutableListOf(firstCalendar), viewModel.calendars.value)

        viewModel.saveNewCalendar()
        testScheduler.triggerActions()

        Mockito.verify(modelMock, timeout(200)).saveNewCalendar(argThat{
            equals(ViewModelImpl.hardcodedCalendarToSave)
        })
        Assert.assertEquals(2, viewModel.calendars.value?.size)
    }
}