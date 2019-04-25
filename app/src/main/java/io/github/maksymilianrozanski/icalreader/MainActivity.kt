package io.github.maksymilianrozanski.icalreader

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.maksymilianrozanski.icalreader.component.AppComponent
import io.github.maksymilianrozanski.icalreader.data.CalendarData
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelFactory
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelImpl
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelInterface
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var calendar: Calendar
    lateinit var viewModel: ViewModelInterface

    private lateinit var eventsLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var calendarsLayoutManager: LinearLayoutManager
    private lateinit var calendarsAdapter: CalendarsAdapter

    lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent = (applicationContext as MyApp).appComponent
        appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewModelImpl::class.java)
        eventsLayoutManager = LinearLayoutManager(this)

        val events: MutableList<CalendarEvent> =
            if (viewModel.eventsData.value?.data?.events != null) viewModel.eventsData.value?.data?.events
                    as MutableList<CalendarEvent> else mutableListOf()
        eventsAdapter = EventsAdapter(this, events)
        recyclerViewId.layoutManager = eventsLayoutManager
        recyclerViewId.adapter = eventsAdapter

        initNavigationDrawer()

        val eventsObserver = Observer<ResponseWrapper<CalendarData>> {
            eventsAdapter.setData(it.data.events)
            when (it.status) {
                "Loading" -> {
                    progressBar.isIndeterminate = true
                    progressBar.progressBackgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }
                "Success" -> {
                    progressBar.isIndeterminate = false
                    progressBar.progressBackgroundTintList = ColorStateList.valueOf(Color.GREEN)
                    scrollToMostRecent()
                }
                else -> {
                    progressBar.isIndeterminate = false
                    progressBar.progressBackgroundTintList = ColorStateList.valueOf(Color.RED)
                }
            }
            Toast.makeText(this, it.status, Toast.LENGTH_LONG).show()
        }
        viewModel.eventsData.observe(this, eventsObserver)

        floatingRefreshButton.setOnClickListener { viewModel.requestCalendarResponse() }
        floatingAddButton.setOnClickListener {
            val manager = supportFragmentManager
            val fragment = AddCalendarDialogFragment()
            fragment.show(manager, "abc")
//            viewModel.saveNewCalendar()
        }
    }

    private fun initNavigationDrawer() {
        calendarsLayoutManager = LinearLayoutManager(this)
        calendarsAdapter = CalendarsAdapter(this, viewModel.calendars.value ?: mutableListOf())
        navigationViewRecyclerView.layoutManager = calendarsLayoutManager
        navigationViewRecyclerView.adapter = calendarsAdapter
        val calendarsObserver = Observer<MutableList<WebCalendar>> {
            if (it != null && it.isNotEmpty()) {
                calendarsAdapter.setData(it)
            }
        }
        viewModel.calendars.observe(this, calendarsObserver)
    }

    private fun scrollToMostRecent() {
        val positionOfEvent = getPositionOfFirstNotFinishedEvent(calendar, eventsAdapter.list)
        (eventsLayoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            positionOfEvent,
            0
        )
    }
}