package io.github.maksymilianrozanski.icalreader.module

import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.icalreader.data.APIService
import io.github.maksymilianrozanski.icalreader.model.Model
import io.github.maksymilianrozanski.icalreader.model.ModelImpl

@Module
class ModelImplModule {

    @Provides
    fun provideModel(apiService: APIService): Model {
        return ModelImpl(apiService)
    }
}