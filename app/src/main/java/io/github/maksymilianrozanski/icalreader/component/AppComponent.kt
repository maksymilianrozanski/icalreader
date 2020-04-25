package io.github.maksymilianrozanski.icalreader.component

import android.app.Application
import android.content.Context
import dagger.Component
import io.github.maksymilianrozanski.icalreader.EventsAdapter
import io.github.maksymilianrozanski.icalreader.MainActivity
import io.github.maksymilianrozanski.icalreader.MyApp
import io.github.maksymilianrozanski.icalreader.model.main.Model
import io.github.maksymilianrozanski.icalreader.module.*
import io.github.maksymilianrozanski.icalreader.viewmodel.BaseSchedulerProvider
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class, ViewModelModule::class, NetworkModule::class, ModelImplModule::class,
        ICalReaderModule::class, DatabaseModule::class, CalendarModule::class, SchedulerProviderModule::class]
)
interface AppComponent {

    fun inject(myApp: MyApp)

    fun inject(mainActivity: MainActivity)

    fun inject(modelImpl: Model)

    fun inject(adapter: EventsAdapter)

    fun getContext(): Context

    fun getApplication(): Application

    fun getModel(): Model

    fun getSchedulerProvider():BaseSchedulerProvider
}