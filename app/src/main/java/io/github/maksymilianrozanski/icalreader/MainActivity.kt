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
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import io.github.maksymilianrozanski.icalreader.data.CalendarResponse
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelFactory
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelImpl
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModelImpl: ViewModelImpl
    private lateinit var layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private lateinit var adapter: EventsAdapter

    lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent = (applicationContext as MyApp).appComponent
        appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModelImpl = ViewModelProviders.of(this, viewModelFactory).get(ViewModelImpl::class.java)
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapter = EventsAdapter(this, viewModelImpl.events.value?.data ?: mutableListOf())

        recyclerViewId.layoutManager = layoutManager
        recyclerViewId.adapter = adapter

        val eventsObserver = Observer<CalendarResponse<MutableList<CalendarEvent>>> {
            if (it?.data != null && it.data.isNotEmpty()) {
                adapter.setData(it.data)
            }
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
        viewModelImpl.events.observe(this, eventsObserver)

        floatingRefreshButton.setOnClickListener { viewModelImpl.requestCalendarResponse() }
    }

    private fun scrollToMostRecent() {
        val positionOfEvent = getPositionOfFirstNotFinishedEvent(adapter.calendar, adapter.list)
        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            positionOfEvent,
            0
        )
    }
}