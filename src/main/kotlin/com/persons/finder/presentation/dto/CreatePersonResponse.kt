package com.persons.finder.presentation.dto

data class CreatePersonResponse(
    val id: Long,
    val name: String,
    val jobTitle: String,
    val hobbies: List<String>,
    val bio: String
)
