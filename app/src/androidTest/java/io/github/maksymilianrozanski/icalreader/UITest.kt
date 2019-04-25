package io.github.maksymilianrozanski.icalreader

import androidx.lifecycle.MutableLiveData

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.nhaarman.mockitokotlin2.argThat
import io.github.maksymilianrozanski.icalreader.component.DaggerUITestAppComponent
import io.github.maksymilianrozanski.icalreader.data.CalendarData
import io.github.maksymilianrozanski.icalreader.data.CalendarForm
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.model.Model
import io.github.maksymilianrozanski.icalreader.module.*
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelInterface
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*

class UITest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    lateinit var app: MyApp
    lateinit var viewModelInterfaceWrapper: ViewModelInterfaceWrapper

    @Mock
    lateinit var viewModelInterfaceMock: ViewModelInterface
    @Mock
    lateinit var eventsDataMock: MutableLiveData<ResponseWrapper<CalendarData>>
    @Mock
    lateinit var calendarsMock: MutableLiveData<MutableList<WebCalendar>>
    @Mock
    lateinit var calendarFormMock: MutableLiveData<CalendarForm>

    @Before
    fun setup() {
        val calendar = Calendar.getInstance()
        MockitoAnnotations.initMocks(this)
        viewModelInterfaceWrapper = ViewModelInterfaceWrapper(viewModelInterfaceMock)

        app = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as MyApp
        val uiTestAppComponent = DaggerUITestAppComponent.builder()
            .appModule(AppModule(app))
            .calendarTestModule(CalendarTestModule(calendar))
            .viewModelTestModule(ViewModelTestModule(viewModelInterfaceWrapper))
            .modelImplTestModule(ModelImplTestModule(Mockito.mock(Model::class.java)))
            .schedulerProviderTestModule(SchedulerProviderTestModule(TestScheduler()))
            .build()

        app.appComponent = uiTestAppComponent
        uiTestAppComponent.inject(this)

        Mockito.`when`(viewModelInterfaceMock.eventsData).thenReturn(eventsDataMock)
        Mockito.`when`(viewModelInterfaceMock.calendars).thenReturn(calendarsMock)
        Mockito.`when`(viewModelInterfaceMock.calendarForm).thenReturn(calendarFormMock)
    }

    @Test
    fun validInputTest() {
        activityRule.launchActivity(null)
        val addCalendarFragment = AddCalendarDialogFragment()
        val manager = activityRule.activity.supportFragmentManager
        addCalendarFragment.show(manager, "abc")

        onView(withId(R.id.calendarNameEditText)).perform(typeText("example name"))
        onView(withId(R.id.calendarUrlEditText)).perform(typeText("http://example.com"))
        onView(withId(R.id.saveCalendar)).perform(click())

        Mockito.verify(calendarFormMock).value = argThat {
            calendarName == "example name" && calendarUrl == "http://example.com"
        }
        Mockito.verify(viewModelInterfaceMock).saveNewCalendarFromLiveData()
    }
}