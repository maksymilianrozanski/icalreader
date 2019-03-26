package io.github.maksymilianrozanski.icalreader

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import io.github.maksymilianrozanski.icalreader.component.DaggerTestAppComponent
import io.github.maksymilianrozanski.icalreader.module.AppModule
import io.github.maksymilianrozanski.icalreader.module.NetworkModule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    lateinit var server: MockWebServer
    lateinit var app: MyApp

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
        //TODO: verify displayed data
        activityRule.launchActivity(null)
        Thread.sleep(2000)
    }
}