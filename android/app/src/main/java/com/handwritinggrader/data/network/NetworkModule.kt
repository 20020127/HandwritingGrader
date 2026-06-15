package com.handwritinggrader.data.network

import com.handwritinggrader.data.api.ApiService
import com.handwritinggrader.data.local.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val BASE_URL = "http://10.0.2.2:8000/"
    
    @Provides
    @Singleton
    fun provideOkHttpClient(settingsDataStore: SettingsDataStore): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val config = runBlocking { settingsDataStore.llmConfig.first() }
                val request = if (config.apiKey.isNotBlank()) {
                    chain.request().newBuilder()
                        .header("X-LLM-Provider", config.provider)
                        .header("X-LLM-Api-Key", config.apiKey)
                        .header("X-LLM-Model", config.model)
                        .header("X-LLM-Base-Url", config.baseUrl)
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
