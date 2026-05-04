package com.persons.finder.presentation.dto

// Response payload for a person found within search radius, includes distance
data class PersonNearbyResponse(
    // Person's unique ID
    val id: Long,
    // Person's full name
    val name: String,
    // Person's job title or profession
    val jobTitle: String,
    // List of hobbies and interests
    val hobbies: List<String>,
    // AI-generated biography
    val bio: String,
    // Person's current latitude
    val latitude: Double,
    // Person's current longitude
    val longitude: Double,
    // Calculated distance from search point in kilometers
    val distanceInKm: Double
)
