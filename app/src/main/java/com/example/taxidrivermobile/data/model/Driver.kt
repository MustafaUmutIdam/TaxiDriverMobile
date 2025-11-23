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