package io.github.maksymilianrozanski.icalreader.module

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.github.maksymilianrozanski.icalreader.data.CalendarData
import io.github.maksymilianrozanski.icalreader.data.CalendarForm
import io.github.maksymilianrozanski.icalreader.data.ResponseWrapper
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelFactory
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelImpl
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelInterface
import javax.inject.Provider
import kotlin.reflect.KClass

@Module
class ViewModelTestModule(var viewModelMock: ViewModelInterfaceWrapper) {

    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    @Retention(AnnotationRetention.RUNTIME)
    @MapKey
    internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

    @Provides
    internal fun viewModelFactory(providerMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): ViewModelFactory {
        return ViewModelFactory(providerMap)
    }

    @Provides
    @IntoMap
    @ViewModelModule.ViewModelKey(ViewModelImpl::class)
    internal fun viewModelImpl(application: Application): ViewModel {
        return viewModelMock
    }
}

class ViewModelInterfaceWrapper(val viewModelInterface: ViewModelInterface) : ViewModel(), ViewModelInterface {

    override val eventsData: MutableLiveData<ResponseWrapper<CalendarData>>
        get() = viewModelInterface.eventsData
    override val calendars: MutableLiveData<MutableList<WebCalendar>>
        get() = viewModelInterface.calendars
    override val calendarForm: MutableLiveData<CalendarForm>
        get() = viewModelInterface.calendarForm

    override fun requestCalendarResponse() {
        viewModelInterface.requestCalendarResponse()
    }

    override fun saveNewCalendar(formToSave: CalendarForm) {
        viewModelInterface.saveNewCalendar(formToSave)
    }

    override fun saveNewCalendar() {
        viewModelInterface.saveNewCalendar()
    }
}