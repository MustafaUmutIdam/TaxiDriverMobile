package com.example.taxidrivermobile.data.api

import com.example.taxidrivermobile.data.model.*
import retrofit2.http.*

interface ApiService {

    @POST("driver-auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("driver-auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("driver-auth/me")
    suspend fun getProfile(@Header("Authorization") token: String): AuthResponse

    // Stations
    @GET("stations")
    suspend fun getStations(): StationsResponse
}
