package io.github.maksymilianrozanski.icalreader.module

import io.github.maksymilianrozanski.icalreader.data.APIService
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Ignore
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkTestModule(val baseUrl: String) {

    @Ignore
    fun provideApi(): APIService {
        return provideRetrofitInterface().create(APIService::class.java)
    }

    @Ignore
    private fun provideRetrofitInterface(): Retrofit {
        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(interceptor)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }
}