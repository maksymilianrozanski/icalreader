package io.github.maksymilianrozanski.icalreader

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.github.maksymilianrozanski.icalreader.component.AppComponent
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
        adapter = EventsAdapter(this, viewModelImpl.events.value ?: mutableListOf())

        recyclerViewId.layoutManager = layoutManager
        recyclerViewId.adapter = adapter

        val eventsObserver = Observer<MutableList<CalendarEvent>> {
            adapter.setData(it!!)
        }
        viewModelImpl.events.observe(this, eventsObserver)
    }
}