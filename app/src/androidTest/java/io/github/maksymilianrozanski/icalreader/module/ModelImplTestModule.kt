package io.github.maksymilianrozanski.icalreader.module

import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.icalreader.model.Model

@Module
class ModelImplTestModule(var modelMock: Model) {

    @Provides
    fun provideModel(): Model {
        return modelMock
    }
}