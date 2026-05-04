package com.persons.finder.domain.services

import com.persons.finder.data.Person
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class PersonsServiceImpl : PersonsService {

    // Thread-safe in-memory storage for persons, keyed by ID
    private val persons = ConcurrentHashMap<Long, Person>()
    // Thread-safe auto-incrementing ID generator starting from 1
    private val idGenerator = AtomicLong(1)

    // Retrieves a person by ID
    override fun getById(id: Long): Person? {
        return persons[id]
    }

    // Saves a person, generating a new ID if needed (when id is 0)
    override fun save(person: Person): Person {
        // Generate new ID for new persons, or use existing ID for updates
        val id = if (person.id == 0L) idGenerator.getAndIncrement() else person.id
        val personWithId = person.copy(id = id)
        persons[id] = personWithId
        return personWithId
    }

    // Returns all persons as a list
    override fun getAll(): List<Person> {
        return persons.values.toList()
    }
}