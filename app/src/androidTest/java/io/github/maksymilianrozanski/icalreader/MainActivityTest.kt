package io.github.maksymilianrozanski.icalreader

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import io.github.maksymilianrozanski.icalreader.component.DaggerTestAppComponent
import io.github.maksymilianrozanski.icalreader.module.AppModule
import io.github.maksymilianrozanski.icalreader.module.NetworkModule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    lateinit var server: MockWebServer
    lateinit var app: MyApp

    //TODO: replace usage of real database
    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        val testHelper = AndroidTestHelper()
        mockResponse.setBody(testHelper.getStringFromFile(InstrumentationRegistry.getContext(), "exampleCalendar.txt"))
        server.enqueue(mockResponse)

        app = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as MyApp

        val mockServerBaseUrl = server.url("/").toString()
        NetworkModule.baseUrl = mockServerBaseUrl

        val testAppComponent = DaggerTestAppComponent.builder()
            .appModule(AppModule(app))
            .build()

        app.appComponent = testAppComponent
        testAppComponent.inject(this)
    }

    @Test
    fun integrationTest() {
        activityRule.launchActivity(null)
        Thread.sleep(500)
        onView(withId(R.id.recyclerViewId)).perform(scrollToPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(0))
            .check(matches(atPosition(0, hasDescendant(withText("Informatyka Lab")))))
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(0, hasDescendant(withText(containsString("14:40"))))))
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(0, hasDescendant(withText(containsString("16:10"))))))
        onView(withId(R.id.recyclerViewId))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(withText(containsString("Sala: San Francisco, Wykładowca: Pierwszy Wykładowca, Kod zajęć: IT 150 Lab 10, Last update: Fri Mar 15 17:38:47 UTC 2019")))
                    )
                )
            )
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(0, hasDescendant(withText(containsString("św. Filipa 17 Kraków"))))))

        onView(withId(R.id.recyclerViewId)).perform(scrollToPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(1))
            .check(matches(atPosition(1, hasDescendant(withText("Historia Wyk")))))
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(1, hasDescendant(withText(containsString("08:00"))))))
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(1, hasDescendant(withText(containsString("09:30"))))))
        onView(withId(R.id.recyclerViewId))
            .check(
                matches(
                    atPosition(
                        1,
                        hasDescendant(withText(containsString("Sala: Praga, Wykładowca: Drugi Wykładowca, Kod zajęć: IT 152 Wyk 2, Last update: Fri Mar 15 17:38:47 UTC 2019")))
                    )
                )
            )
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(1, hasDescendant(withText(containsString("św. Filipa 17 Kraków"))))))

        onView(withId(R.id.recyclerViewId)).perform(scrollToPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(2))
            .check(matches(atPosition(2, hasDescendant(withText("Chemia")))))
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(2, hasDescendant(withText(containsString("16:20"))))))
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(2, hasDescendant(withText(containsString("17:50"))))))
        onView(withId(R.id.recyclerViewId))
            .check(
                matches(
                    atPosition(
                        2,
                        hasDescendant(withText(containsString("Sala: Praga, Wykładowca: Trzeci Wykładowca, Kod zajęć: IT 200 Wyk 3, Last update: Fri Mar 15 17:38:47 UTC 2019")))
                    )
                )
            )
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(2, hasDescendant(withText(containsString("św. Filipa 17 Kraków"))))))

        onView(withId(R.id.recyclerViewId)).perform(scrollToPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(3))
            .check(matches(atPosition(3, hasDescendant(withText("Biologia Ćw")))))
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(3, hasDescendant(withText(containsString("08:00"))))))
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(3, hasDescendant(withText(containsString("09:30"))))))
        onView(withId(R.id.recyclerViewId))
            .check(
                matches(
                    atPosition(
                        3,
                        hasDescendant(withText(containsString("Sala: Warszawa, Wykładowca: Czwarty Wykładowca, Kod zajęć: IT 220 Ćw 7, Last update: Fri Mar 15 17:38:47 UTC 2019")))
                    )
                )
            )
        onView(withId(R.id.recyclerViewId))
            .check(matches(atPosition(3, hasDescendant(withText(containsString("św. Filipa 17 Kraków"))))))
    }
}