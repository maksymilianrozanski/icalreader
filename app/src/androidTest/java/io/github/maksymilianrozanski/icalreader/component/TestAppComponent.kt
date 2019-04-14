package io.github.maksymilianrozanski.icalreader.component

import dagger.Component
import io.github.maksymilianrozanski.icalreader.MainActivityTest
import io.github.maksymilianrozanski.icalreader.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class, NetworkModule::class, ModelImplModule::class
    ,ICalReaderModule::class, DatabaseTestModule::class, CalendarTestModule::class])
interface TestAppComponent : AppComponent {

    fun inject(test: MainActivityTest)
}