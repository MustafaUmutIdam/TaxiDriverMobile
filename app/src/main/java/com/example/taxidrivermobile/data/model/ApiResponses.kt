package com.example.taxidrivermobile.data.model

data class TripResponse(
    val success: Boolean,
    val message: String?,
    val data: Trip?
)

data class UpdateLocationRequest(
    val lat: Double,
    val lng: Double
)

data class UpdateStatusRequest(
    val status: String
)