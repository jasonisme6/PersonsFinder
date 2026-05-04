package com.persons.finder.data

// Represents a geographic location with coordinates
data class Location(
    // Reference ID linking this location to a person (uses Person's ID)
    val referenceId: Long,
    // Latitude coordinate (-90 to 90 degrees)
    val latitude: Double,
    // Longitude coordinate (-180 to 180 degrees)
    val longitude: Double
)
