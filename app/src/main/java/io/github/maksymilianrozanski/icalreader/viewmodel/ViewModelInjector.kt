package io.github.maksymilianrozanski.icalreader.viewmodel

import dagger.Component
import io.github.maksymilianrozanski.icalreader.module.NetworkModule
import io.github.maksymilianrozanski.icalreader.module.ModelImplModule
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, ModelImplModule::class])
interface ViewModelInjector {
    /**
     * Injects required dependencies into the specified ViewModelImpl.
     * @param viewModelImpl ViewModelImpl in which to inject the dependencies
     */
    fun inject(viewModelImpl: ViewModelImpl)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}