package com.persons.finder.presentation.dto

data class CreatePersonRequest(
    val name: String,
    val jobTitle: String,
    val hobbies: List<String>,
    val latitude: Double,
    val longitude: Double
)
