package io.github.maksymilianrozanski.icalreader

import android.widget.Button
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.times
import io.github.maksymilianrozanski.icalreader.component.DaggerUITestAppComponent
import io.github.maksymilianrozanski.icalreader.data.CalendarData
import io.github.maksymilianrozanski.icalreader.data.CalendarForm
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.model.Model
import io.github.maksymilianrozanski.icalreader.module.*
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelInterface
import org.hamcrest.CoreMatchers.*
import org.junit.Assert
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

    @get:Rule
    val executor = InstantTaskExecutorRule()

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
            .trampolineSchedulerProviderTestModule(TrampolineSchedulerProviderTestModule())
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
        Mockito.verify(viewModelInterfaceMock).saveNewCalendar(argThat<CalendarForm> {
            calendarName == "example name" && calendarUrl == "http://example.com"
        })
    }

    @Test
    fun closingDialogAfterInputTest() {
        val liveData = MutableLiveData<CalendarForm>()
        Mockito.`when`(viewModelInterfaceMock.calendarForm).thenReturn(liveData)

        activityRule.launchActivity(null)

        val addCalendarFragment = AddCalendarDialogFragment()
        val manager = activityRule.activity.supportFragmentManager
        addCalendarFragment.show(manager, "abc")

        onView(withId(R.id.calendarNameEditText)).perform(typeText("example name"))
        onView(withId(R.id.calendarUrlEditText)).perform(typeText("http://example.com"))
        onView(withId(R.id.saveCalendar)).perform(click())

        Assert.assertTrue(liveData.value!!.calendarName == "example name" && liveData.value!!.calendarUrl == "http://example.com")
        Mockito.verify(viewModelInterfaceMock).saveNewCalendar(argThat {
            calendarName == "example name" && calendarUrl == "http://example.com"
        })

        val successStatus = CalendarForm("example name", "http://example.com")
        successStatus.nameStatus = CalendarForm.saved
        successStatus.urlStatus = CalendarForm.saved
        activityRule.runOnUiThread { liveData.value = successStatus }

        onView(withId(R.id.saveCalendar)).check(doesNotExist())

        addCalendarFragment.show(manager, "abc")
        onView(withId(R.id.calendarNameEditText)).check(matches(withText("")))
        onView(withId(R.id.calendarUrlEditText)).check(matches(withText("")))
    }

    @Test
    fun blankCalendarNameTest() {
        val calendarFormObject = MutableLiveData<CalendarForm>()
        Mockito.`when`(viewModelInterfaceMock.calendarForm).thenReturn(calendarFormObject)
        activityRule.launchActivity(null)
        val addCalendarFragment = AddCalendarDialogFragment()
        val manager = activityRule.activity.supportFragmentManager
        addCalendarFragment.show(manager, "abc")

        onView(withId(R.id.calendarNameEditText)).perform(click())
        onView(withId(R.id.calendarUrlEditText)).perform(typeText("http://example.com"))
        onView(withId(R.id.saveCalendar)).perform(click())

        onView(withId(R.id.calendarNameEditText)).check(matches(hasErrorText("Cannot be blank")))
        onView(withId(R.id.calendarUrlEditText)).check(matches(not(hasErrorText(containsString("")))))

        Mockito.verify(viewModelInterfaceMock, times(0)).saveNewCalendar(any())
    }

    @Test
    fun requestingCalendarFromDrawerTest() {
        val calendarOne = WebCalendar(calendarName = "Calendar One", calendarUrl = "http://example.com")
        val calendarTwo = WebCalendar(calendarName = "Calendar Two", calendarUrl = "http://example.com")
        val savedCalendars = mutableListOf(calendarOne, calendarTwo)
        Mockito.`when`(calendarsMock.value).thenReturn(savedCalendars)

        activityRule.launchActivity(null)

        onView(withId(R.id.drawerLayout)).check(matches(isClosed()))
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withId(R.id.drawerLayout)).check(matches(isOpen()))

        onView(withText("Calendar One")).check(matches(isDisplayed()))
        onView(withText("Calendar Two")).check(matches(isDisplayed()))

        onView(withText("Calendar Two")).perform(click())
        Mockito.verify(viewModelInterfaceMock).requestSavedCalendarData(argThat { equals(calendarTwo) })

        onView(withId(R.id.drawerLayout)).check(matches(isClosed()))
    }

    @Test
    fun deleteDialogTest() {
        val calendarOne = WebCalendar(calendarName = "Calendar One", calendarUrl = "http://example.com")
        val savedCalendars = mutableListOf(calendarOne)
        Mockito.`when`(calendarsMock.value).thenReturn(savedCalendars)

        activityRule.launchActivity(null)
        val delete = getInstrumentation().targetContext.getString(R.string.delete)
        val cancel = getInstrumentation().targetContext.getString(R.string.cancel)
        val areYouSure = getInstrumentation().targetContext.getString(R.string.are_you_sure_you_want_to_delete)

        onView(withId(R.id.drawerLayout)).check(matches(isClosed()))
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withId(R.id.drawerLayout)).check(matches(isOpen()))
        onView(withText("Calendar One")).check(matches(isDisplayed()))
        onView(withId(R.id.deleteCalendarButton)).check(matches(isDisplayed()))
        onView(withId(R.id.deleteCalendarButton)).perform(click())
        onView(withText("$areYouSure Calendar One?")).check(matches(isDisplayed()))
        onView(allOf(instanceOf(Button::class.java), withText(delete))).check(matches(isDisplayed()))
        onView(withText(cancel)).check(matches(isDisplayed()))

        onView(withText(cancel)).perform(click())
        Mockito.verify(viewModelInterfaceMock, times(0)).deleteCalendar(any())

        onView(withId(R.id.deleteCalendarButton)).perform(click())
        onView(allOf(instanceOf(Button::class.java), withText(delete))).perform(click())
        Mockito.verify(viewModelInterfaceMock).deleteCalendar(calendarOne)
        onView(withId(R.id.drawerLayout)).check(matches(isClosed()))
    }
}