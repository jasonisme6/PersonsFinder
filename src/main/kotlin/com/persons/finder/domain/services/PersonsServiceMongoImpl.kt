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
        return personRepository.findById(id.toString())
            .map { it.toPerson() }
            .orElse(null)
    }

    override fun save(person: Person): Person {
        val document = PersonDocument(
            id = if (person.id == 0L) null else person.id.toString(),
            name = person.name,
            jobTitle = person.jobTitle,
            hobbies = person.hobbies,
            bio = person.bio,
            location = person.location?.toGeo()
        )

        val savedDocument = personRepository.save(document)
        return savedDocument.toPerson()
    }

    override fun getAll(): List<Person> {
        return personRepository.findAll().map { it.toPerson() }
    }
}
