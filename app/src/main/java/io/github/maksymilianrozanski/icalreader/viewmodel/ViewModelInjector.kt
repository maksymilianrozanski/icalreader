package io.github.maksymilianrozanski.icalreader.viewmodel

import dagger.Component
import io.github.maksymilianrozanski.icalreader.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, ModelImplModule::class, ICalReaderModule::class, DatabaseModule::class])
interface ViewModelInjector {
    /**
     * Injects required dependencies into the specified ViewModelImpl.
     * @param viewModelImpl ViewModelImpl in which to inject the dependencies
     */
    fun inject(viewModelImpl: ViewModelImpl)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun appModule(appModule: AppModule):Builder
        fun modelImplModule(modelImplModule: ModelImplModule): Builder
    }
}