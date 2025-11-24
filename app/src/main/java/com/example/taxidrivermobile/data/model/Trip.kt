package com.example.taxidrivermobile.data.model

data class Trip(
    val _id: String? = null,
    val station: Station? = null,
    val driver: Driver? = null, // Reverted back to Driver? for ActiveTripScreen
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
    val currentAttempt: Int? = null,
    val maxAttempts: Int? = null,
    val assignmentExpiry: String? = null,
    val rejectedDrivers: List<RejectedDriver>? = null,
    val cancellationReason: String? = null,
    val cancelledBy: String? = null,
    val notes: String? = null,
    val paymentStatus: String? = null,
    val fareDetails: FareDetails? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)


data class Customer(
    val name: String?,
    val phone: String
)

data class TripLocation(
    val address: String,
    val location: Location
)

data class RejectedDriver(
    val driver: String,
    val rejectedAt: String,
    val reason: String?
)

data class FareDetails(
    val baseRate: Double,
    val perKmRate: Double,
    val distance: Double,
    val isNightTime: Boolean,
    val nightSurcharge: Double,
    val total: Double
)
data class CreatedBy(
    val _id: String? = null,
    val fullName: String? = null,
    val email: String? = null
)
