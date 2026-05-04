package com.persons.finder.presentation.dto

// Request payload for updating a person's location
data class UpdateLocationRequest(
    // New latitude coordinate
    val latitude: Double,
    // New longitude coordinate
    val longitude: Double
)
