package io.github.maksymilianrozanski.icalreader.module


import android.arch.lifecycle.ViewModel
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelFactory
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelImpl
import javax.inject.Provider
import kotlin.reflect.KClass

@Module
class ViewModelModule {

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
    @ViewModelKey(ViewModelImpl::class)
    internal fun viewModelImpl(): ViewModel {
        return ViewModelImpl()
    }
}
