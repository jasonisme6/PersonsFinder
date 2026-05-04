package com.persons.finder.data

// Represents a person in the system with their profile information
data class Person(
    // Unique identifier for the person
    val id: Long,
    // Person's full name
    val name: String,
    // Person's job title or profession
    val jobTitle: String,
    // List of hobbies and interests
    val hobbies: List<String>,
    // AI-generated biography based on job title and hobbies
    val bio: String,
    // Current location (nullable, can be updated)
    var location: Location? = null
)
