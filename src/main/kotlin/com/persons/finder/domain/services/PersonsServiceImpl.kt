package com.persons.finder.domain.services

import com.persons.finder.data.Person
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class PersonsServiceImpl : PersonsService {

    private val persons = ConcurrentHashMap<Long, Person>()
    private val idGenerator = AtomicLong(1)

    override fun getById(id: Long): Person? {
        return persons[id]
    }

    override fun save(person: Person): Person {
        val id = if (person.id == 0L) idGenerator.getAndIncrement() else person.id
        val personWithId = person.copy(id = id)
        persons[id] = personWithId
        return personWithId
    }

    override fun getAll(): List<Person> {
        return persons.values.toList()
    }
}