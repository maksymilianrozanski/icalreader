package io.github.maksymilianrozanski.icalreader.module

import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.icalreader.data.APIService
import io.github.maksymilianrozanski.icalreader.model.ICalReader
import io.github.maksymilianrozanski.icalreader.model.Model
import io.github.maksymilianrozanski.icalreader.model.ModelImpl
import io.github.maksymilianrozanski.icalreader.model.storage.EventDao

@Module
class ModelImplModule {

    @Provides
    fun provideModel(apiService: APIService, iCalReader: ICalReader, dataSource:EventDao): Model {
        return ModelImpl(apiService, iCalReader, dataSource)
    }
}