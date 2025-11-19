package com.example.taxidrivermobile.data.model

data class Station(
    val _id: String,
    val name: String,
    val address: String,
    val phone: String,
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)