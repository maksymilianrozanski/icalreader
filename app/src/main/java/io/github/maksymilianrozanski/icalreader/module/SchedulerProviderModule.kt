package io.github.maksymilianrozanski.icalreader.module

import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.icalreader.viewmodel.BaseSchedulerProvider
import io.github.maksymilianrozanski.icalreader.viewmodel.SchedulerProvider

@Module
class SchedulerProviderModule {

    @Provides
    fun provideSchedulerProvider(): BaseSchedulerProvider {
        return SchedulerProvider()
    }
}