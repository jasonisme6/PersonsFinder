package com.persons.finder.domain.services

import com.persons.finder.data.Person

// Service interface for managing person records
interface PersonsService {
    // Retrieves a person by their unique ID, returns null if not found
    fun getById(id: Long): Person?
    // Saves or updates a person record, returns the saved person with ID
    fun save(person: Person): Person
    // Retrieves all persons in the system
    fun getAll(): List<Person>
}