package com.persons.finder.domain.services

interface AIBioService {
    fun generateBio(jobTitle: String, hobbies: List<String>): String
}
