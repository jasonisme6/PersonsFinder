package com.persons.finder.domain.services

import com.persons.finder.data.Person
import com.persons.finder.data.PersonDocument
import com.persons.finder.data.PersonRepository
import com.persons.finder.data.toGeo
import com.persons.finder.data.toPerson
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

/**
 * MongoDB implementation of PersonsService
 */
@Service
@Primary
class PersonsServiceMongoImpl(
    private val personRepository: PersonRepository
) : PersonsService {

    override fun getById(id: Long): Person? {
        return personRepository.findByNumericId(id)?.toPerson()
    }

    override fun save(person: Person): Person {
        // Check if this is an update (person.id != 0) or a new person
        if (person.id != 0L) {
            // Update: find existing document and preserve its MongoDB ID
            val existingDoc = personRepository.findByNumericId(person.id)
            if (existingDoc != null) {
                // Update existing document
                val updatedDoc = PersonDocument(
                    id = existingDoc.id, // Keep original MongoDB ID
                    numericId = person.id,
                    name = person.name,
                    jobTitle = person.jobTitle,
                    hobbies = person.hobbies,
                    bio = person.bio,
                    location = person.location?.toGeo()
                )
                val savedDocument = personRepository.save(updatedDoc)
                return savedDocument.toPerson()
            } else {
                // numericId not found, cannot update non-existent person
                throw IllegalArgumentException("Person with id ${person.id} not found")
            }
        } else {
            // Create new person with unique numericId
            val numericId = System.nanoTime() + Thread.currentThread().id
            val document = PersonDocument(
                id = null, // Let MongoDB generate ID
                numericId = numericId,
                name = person.name,
                jobTitle = person.jobTitle,
                hobbies = person.hobbies,
                bio = person.bio,
                location = person.location?.toGeo()
            )
            val savedDocument = personRepository.save(document)
            return savedDocument.toPerson()
        }
    }

    override fun getAll(): List<Person> {
        return personRepository.findAll().map { it.toPerson() }
    }
}
