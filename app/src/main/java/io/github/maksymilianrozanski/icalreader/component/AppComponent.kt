package io.github.maksymilianrozanski.icalreader.component

import android.app.Application
import android.content.Context
import dagger.Component
import io.github.maksymilianrozanski.icalreader.MyApp
import io.github.maksymilianrozanski.icalreader.module.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(myApp: MyApp)

    fun getContext(): Context

    fun getApplication(): Application

}