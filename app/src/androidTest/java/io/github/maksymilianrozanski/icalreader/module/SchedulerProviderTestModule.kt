package io.github.maksymilianrozanski.icalreader.module

import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.icalreader.viewmodel.BaseSchedulerProvider
import io.github.maksymilianrozanski.icalreader.viewmodel.TestSchedulerProvider
import io.github.maksymilianrozanski.icalreader.viewmodel.TrampolineScheduleProvider
import io.reactivex.schedulers.TestScheduler

@Module
class SchedulerProviderTestModule(var scheduler: TestScheduler) {

    @Provides
    fun provideTestSchedulerProvider(): BaseSchedulerProvider {
        return TestSchedulerProvider(scheduler)
    }
}

@Module
class TrampolineSchedulerProviderTestModule {

    @Provides
    fun provideTrampolineSchedulerProvider(): BaseSchedulerProvider {
        return TrampolineScheduleProvider()
    }
}

