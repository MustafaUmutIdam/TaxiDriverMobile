package com.example.taxidrivermobile.data.model

data class TripsResponse(
    val success: Boolean,
    val count: Int,
    val data: List<Trip>
)
