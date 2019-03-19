package io.github.maksymilianrozanski.icalreader.module

import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.icalreader.model.ICalReader
import io.github.maksymilianrozanski.icalreader.model.ICalReaderImpl

@Module
class ICalReaderModule {

    @Provides
    fun provideICalReader(): ICalReader {
        return ICalReaderImpl()
    }
}