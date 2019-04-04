package io.github.maksymilianrozanski.icalreader

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
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
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: EventsAdapter

    lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent = (applicationContext as MyApp).appComponent
        appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModelImpl = ViewModelProviders.of(this, viewModelFactory).get(ViewModelImpl::class.java)
        layoutManager = LinearLayoutManager(this)
        adapter = EventsAdapter(this, viewModelImpl.events.value?.data ?: mutableListOf())

        recyclerViewId.layoutManager = layoutManager
        recyclerViewId.adapter = adapter

        val eventsObserver = Observer<CalendarResponse<MutableList<CalendarEvent>>> {
            if (it!!.data.isNotEmpty()){
                adapter.setData(it.data)
            }
            Toast.makeText(this, it.status, Toast.LENGTH_LONG).show()
        }
        viewModelImpl.events.observe(this, eventsObserver)

        floatingRefreshButton.setOnClickListener { viewModelImpl.requestEvents() }
    }
}