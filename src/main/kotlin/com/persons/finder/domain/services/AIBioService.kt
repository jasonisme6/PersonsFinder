package com.persons.finder.domain.services

// Service interface for AI-powered biography generation
interface AIBioService {
    // Generates a creative biography based on job title and hobbies
    fun generateBio(jobTitle: String, hobbies: List<String>): String
}
