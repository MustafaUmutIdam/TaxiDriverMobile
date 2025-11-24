package com.example.taxidrivermobile.data.model

data class Driver(
    val _id: String? = null,
    val fullName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val licenseNumber: String? = null,
    val vehiclePlate: String? = null,
    val station: Station? = null,
    val status: String? = null,
    val currentLocation: Location? = null,
    val rating: Double? = null,
    val totalTrips: Int? = null,
    val balance: Double? = null
)
data class MeResponse(
    val success: Boolean,
    val data: Driver
)

data class StatusRequest(val status: String)

data class StatusResponse(
    val success: Boolean,
    val message: String,
    val data: Driver?
)

data class StatusUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: DriverStatusUpdate?
)

data class DriverStatusUpdate(
    val station: String? = null,
    val status: String? = null
)

data class DriverStatusData(
    val _id: String,
    val name: String?,
    val phone: String?,
    val status: String
)