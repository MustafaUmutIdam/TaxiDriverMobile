package com.example.taxidrivermobile.data.api

import com.example.taxidrivermobile.data.model.*
import retrofit2.http.*

interface ApiService {

    // Driver Auth
    @POST("driver-auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("driver-auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("driver-auth/me")
    suspend fun getProfile(@Header("Authorization") token: String): MeResponse

    @PUT("driver-auth/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest,
        @Header("Authorization") token: String
    ): AuthResponse

    @PUT("driver-auth/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
        @Header("Authorization") token: String
    ): ChangePasswordResponse

    @PATCH("driver-auth/status")
    suspend fun updateStatus(
        @Body request: UpdateStatusRequest,
        @Header("Authorization") token: String
    ): StatusUpdateResponse




    // Stations
    @GET("stations")
    suspend fun getStations(): StationsResponse

    // Driver Location & Status
    @PATCH("drivers/{driverId}/location")
    suspend fun updateLocation(
        @Path("driverId") driverId: String,
        @Body request: UpdateLocationRequest,
        @Header("Authorization") token: String
    ): AuthResponse

//    @PATCH("drivers/{driverId}/status")
//    suspend fun updateStatus(
//        @Path("driverId") driverId: String,
//        @Body request: UpdateStatusRequest,
//        @Header("Authorization") token: String
//    ): AuthResponse

    // Driver Trips
    @GET("driver-trips/active")
    suspend fun getActiveTrip(
        @Query("status") status: String?,
        @Header("Authorization") token: String
    ): TripResponse

    @GET("driver-trips/{tripId}")
    suspend fun getTrip(
        @Path("tripId") tripId: String,
        @Header("Authorization") token: String
    ): TripResponse

    @POST("driver-trips/{tripId}/accept")
    suspend fun acceptTrip(
        @Path("tripId") tripId: String,
        @Header("Authorization") token: String
    ): TripResponse

    @POST("driver-trips/{tripId}/reject")
    suspend fun rejectTrip(
        @Path("tripId") tripId: String,
        @Body reason: Map<String, String>,
        @Header("Authorization") token: String
    ): TripResponse

    @POST("driver-trips/{tripId}/start")
    suspend fun startTrip(
        @Path("tripId") tripId: String,
        @Header("Authorization") token: String
    ): TripResponse

    @POST("driver-trips/{tripId}/complete")
    suspend fun completeTrip(
        @Path("tripId") tripId: String,
        @Body actualFare: Map<String, Double>,
        @Header("Authorization") token: String
    ): TripResponse
}
