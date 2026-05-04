package com.persons.finder.presentation.dto

// Request payload for creating a new person with initial location
data class CreatePersonRequest(
    // Person's full name
    val name: String,
    // Person's job title or profession
    val jobTitle: String,
    // List of hobbies and interests
    val hobbies: List<String>,
    // Initial latitude coordinate
    val latitude: Double,
    // Initial longitude coordinate
    val longitude: Double
)
