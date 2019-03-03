package io.github.maksymilianrozanski.icalreader.module

import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.icalreader.data.ApiUtils
@Module
class ApiUtilsModule {

    @Provides
    fun provideApiUtils(): ApiUtils {

        return ApiUtils()
    }
}