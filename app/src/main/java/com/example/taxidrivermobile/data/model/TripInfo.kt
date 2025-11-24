package com.example.taxidrivermobile.data.model

// Data classes for the simplified trip list from /driver-auth/trips

data class TripsInfoResponse(
    val success: Boolean,
    val count: Int,
    val data: List<TripInfo>
)

data class TripInfo(
    val _id: String? = null,
    val station: Station? = null,
    val driver: String? = null, // The driver is a String ID in this response
    val createdBy: CreatedBy? = null,
    val status: String? = null,
    val customer: Customer? = null,
    val pickup: TripLocation? = null,
    val dropoff: TripLocation? = null,
    val distance: Double? = null,
    val estimatedDuration: Int? = null,
    val estimatedFare: Double? = null,
    val actualFare: Double? = null,
    val requestedAt: String? = null,
    val assignedAt: String? = null,
    val acceptedAt: String? = null,
    val startedAt: String? = null,
    val completedAt: String? = null,
    val paymentStatus: String? = null,
    val fareDetails: FareDetails? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
