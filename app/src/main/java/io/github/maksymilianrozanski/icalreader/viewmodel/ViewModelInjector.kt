package io.github.maksymilianrozanski.icalreader.viewmodel

import dagger.Component
import io.github.maksymilianrozanski.icalreader.annotation.ViewModelScope
import io.github.maksymilianrozanski.icalreader.component.AppComponent

@ViewModelScope
@Component(dependencies = [AppComponent::class])
interface ViewModelInjector {
    /**
     * Injects required dependencies into the specified ViewModelImpl.
     * @param viewModelImpl ViewModelImpl in which to inject the dependencies
     */
    fun inject(viewModelImpl: ViewModelImpl)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector
        fun appComponent(appComponent: AppComponent): Builder
    }
}