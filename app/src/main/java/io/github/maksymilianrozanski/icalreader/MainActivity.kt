package io.github.maksymilianrozanski.icalreader

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelImpl
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModelImpl: ViewModelImpl
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModelImpl = ViewModelProviders.of(this).get(ViewModelImpl::class.java)
        layoutManager = LinearLayoutManager(this)
        adapter = EventsAdapter(this, viewModelImpl.events.value ?: mutableListOf())

        recyclerViewId.layoutManager = layoutManager
        recyclerViewId.adapter = adapter

        val eventsObserver = Observer<MutableList<CalendarEvent>> {
            adapter.setData(it!!)
        }
        viewModelImpl.events.observe(this, eventsObserver)
    }
}