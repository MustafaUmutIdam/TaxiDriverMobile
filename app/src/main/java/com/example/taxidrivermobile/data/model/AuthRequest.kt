package com.example.taxidrivermobile.data.model

data class LoginRequest(
    val phone: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val phone: String,
    val email: String?,
    val password: String,
    val licenseNumber: String,
    val vehiclePlate: String,
    val station: String // Station ID
)

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val data: AuthData?
)

data class AuthData(
    val driver: Driver,
    val token: String
)

data class StationsResponse(
    val success: Boolean,
    val data: List<Station>
)