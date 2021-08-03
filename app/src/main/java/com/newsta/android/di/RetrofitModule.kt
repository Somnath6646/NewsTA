package com.newsta.android.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.newsta.android.BuildConfig
import com.newsta.android.remote.services.AuthenticationService
import com.newsta.android.remote.services.NewsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RetrofitModule {
    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit.Builder {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
        }.build()

        return Retrofit.Builder()
            .baseUrl("http://newsta.in/")
            .client( client
            )

            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideAuthenticationService(retrofit: Retrofit.Builder): AuthenticationService{
        return retrofit
            .build()
            .create(AuthenticationService::class.java)
    }

    @Singleton
    @Provides
    fun provideNewsService(retrofit: Retrofit.Builder): NewsService {
        return retrofit
                .build()
                .create(NewsService::class.java)
    }

}
