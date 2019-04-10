package io.github.maksymilianrozanski.icalreader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.github.maksymilianrozanski.icalreader.MyApp
import io.github.maksymilianrozanski.icalreader.module.AppModule

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val injector: ViewModelInjector = DaggerViewModelInjector.builder()
        .appModule(AppModule(application as MyApp))
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