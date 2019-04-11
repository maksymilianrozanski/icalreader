package io.github.maksymilianrozanski.icalreader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.github.maksymilianrozanski.icalreader.MyApp

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val injector: ViewModelInjector = DaggerViewModelInjector.builder()
        .appComponent((application as MyApp).appComponent)
        .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is ViewModelImpl -> injector.inject(this)
        }
    }
}