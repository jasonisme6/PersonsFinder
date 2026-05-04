package com.persons.finder.presentation.dto

// Response payload after successfully creating a person
data class CreatePersonResponse(
    // Generated unique ID for the person
    val id: Long,
    // Person's full name
    val name: String,
    // Person's job title or profession
    val jobTitle: String,
    // List of hobbies and interests
    val hobbies: List<String>,
    // AI-generated biography
    val bio: String
)
