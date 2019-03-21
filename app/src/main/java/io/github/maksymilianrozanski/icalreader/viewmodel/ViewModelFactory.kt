package io.github.maksymilianrozanski.icalreader.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import javax.inject.Inject
import javax.inject.Provider

class ViewModelFactory @Inject
constructor(private val providerMap: Map<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return providerMap.getValue(modelClass).get() as T
    }
}