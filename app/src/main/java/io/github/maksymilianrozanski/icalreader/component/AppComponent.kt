package io.github.maksymilianrozanski.icalreader.component

import android.app.Application
import android.content.Context
import dagger.Component
import io.github.maksymilianrozanski.icalreader.MyApp
import io.github.maksymilianrozanski.icalreader.data.ApiUtils
import io.github.maksymilianrozanski.icalreader.module.ApiUtilsModule
import io.github.maksymilianrozanski.icalreader.module.AppModule
import javax.inject.Singleton

@Singleton
@Component( modules = [AppModule::class, ApiUtilsModule::class])
interface AppComponent {

    fun inject(myApp: MyApp)

    fun getContext(): Context

    fun getApplication(): Application

    fun getApiUtils(): ApiUtils

}