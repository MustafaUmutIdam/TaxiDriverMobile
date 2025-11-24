package com.example.taxidrivermobile.di

import android.content.Context
import com.example.taxidrivermobile.data.api.ApiService
import com.example.taxidrivermobile.data.local.TokenManager
import com.example.taxidrivermobile.data.repository.AuthRepository
import com.example.taxidrivermobile.data.repository.ProfileRepository
import com.example.taxidrivermobile.data.repository.TripRepository
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    @Singleton
    @Provides
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val token = tokenManager.getToken()
                if (token != null) {
                    request.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(request.build())
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(apiService: ApiService, tokenManager: TokenManager): AuthRepository {
        return AuthRepository(apiService, tokenManager)
    }

    @Singleton
    @Provides
    fun provideTripRepository(apiService: ApiService, tokenManager: TokenManager): TripRepository {
        return TripRepository(apiService, tokenManager)
    }

    @Singleton
    @Provides
    fun provideProfileRepository(apiService: ApiService,tokenManager: TokenManager): ProfileRepository {
        return ProfileRepository(apiService, tokenManager)
    }
}