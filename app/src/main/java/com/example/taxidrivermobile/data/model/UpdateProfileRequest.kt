package com.example.taxidrivermobile.data.model

data class UpdateProfileRequest(
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val licenseNumber: String? = null,
    val vehiclePlate: String? = null
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val success: Boolean,
    val message: String?,
    val data: TokenData?
)

data class TokenData(
    val token: String
)