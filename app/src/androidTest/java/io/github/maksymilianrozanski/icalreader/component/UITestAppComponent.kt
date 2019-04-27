package io.github.maksymilianrozanski.icalreader.component

import dagger.Component
import io.github.maksymilianrozanski.icalreader.UITest
import io.github.maksymilianrozanski.icalreader.module.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class, ViewModelTestModule::class, CalendarTestModule::class,
        ModelImplTestModule::class, TrampolineSchedulerProviderTestModule::class]
)
interface UITestAppComponent : AppComponent {

    fun inject(test: UITest)
}