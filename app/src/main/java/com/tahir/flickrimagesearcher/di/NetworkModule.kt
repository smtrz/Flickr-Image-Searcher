package com.tahir.flickrimagesearcher.di

import com.tahir.flickrimagesearcher.data.source.remote.api.FlickrApiService
import com.tahir.flickrimagesearcher.util.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {

    @Single
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

            .build()
    }

    @Single
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Single
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Single
    fun provideApiService(retrofit: Retrofit): FlickrApiService {
        return retrofit.create(FlickrApiService::class.java)
    }
}