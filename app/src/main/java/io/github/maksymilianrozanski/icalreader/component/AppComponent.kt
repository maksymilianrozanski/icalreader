package io.github.maksymilianrozanski.icalreader.component

import android.app.Application
import android.content.Context
import dagger.Component
import io.github.maksymilianrozanski.icalreader.MainActivity
import io.github.maksymilianrozanski.icalreader.MyApp
import io.github.maksymilianrozanski.icalreader.model.Model
import io.github.maksymilianrozanski.icalreader.module.AppModule
import io.github.maksymilianrozanski.icalreader.module.ICalReaderModule
import io.github.maksymilianrozanski.icalreader.module.ModelImplModule
import io.github.maksymilianrozanski.icalreader.module.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, ModelImplModule::class, ICalReaderModule::class])
interface AppComponent {

    fun inject(myApp: MyApp)

    fun inject(mainActivity: MainActivity)

    fun inject(modelImpl: Model)

    fun getContext(): Context

    fun getApplication(): Application

}