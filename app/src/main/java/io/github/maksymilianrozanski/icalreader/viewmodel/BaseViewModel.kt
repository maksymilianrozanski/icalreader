package io.github.maksymilianrozanski.icalreader.viewmodel

import android.arch.lifecycle.ViewModel
import io.github.maksymilianrozanski.icalreader.DaggerViewModelInjector
import io.github.maksymilianrozanski.icalreader.NetworkModule

abstract class BaseViewModel:ViewModel(){
    private val injector: ViewModelInjector = DaggerViewModelInjector.builder()
        .networkModule(NetworkModule)
        .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is PostListViewModel -> injector.inject(this)
        }
    }
}