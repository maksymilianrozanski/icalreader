package io.github.maksymilianrozanski.icalreader

import android.app.Application
import android.content.Context
import io.github.maksymilianrozanski.icalreader.component.AppComponent
import io.github.maksymilianrozanski.icalreader.component.DaggerAppComponent
import io.github.maksymilianrozanski.icalreader.module.AppModule

class MyApp : Application(){

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    companion object {
        fun get(context: Context): MyApp {
            return context.applicationContext as MyApp
        }
    }
}