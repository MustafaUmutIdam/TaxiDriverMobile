package com.example.taxidrivermobile.data.model

data class Driver(
    val _id: String,
    val fullName: String,
    val phone: String,
    val email: String?,
    val licenseNumber: String,
    val vehiclePlate: String,
    val station: Station,
    val status: String, // active, busy, offline, break
    val currentLocation: Location?,
    val rating: Double,
    val totalTrips: Int,
    val balance: Double
)