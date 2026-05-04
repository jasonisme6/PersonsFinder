package com.persons.finder.presentation.dto

data class PersonNearbyResponse(
    val id: Long,
    val name: String,
    val jobTitle: String,
    val hobbies: List<String>,
    val bio: String,
    val latitude: Double,
    val longitude: Double,
    val distanceInKm: Double
)
