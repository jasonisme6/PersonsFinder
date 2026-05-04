package com.persons.finder

import com.persons.finder.data.Location
import com.persons.finder.data.PersonDocument
import com.persons.finder.data.PersonRepository
import com.persons.finder.data.toGeo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for MongoDB operations with Testcontainers
 * NOTE: These tests require Docker to be running.
 * Remove @Disabled annotation to run these tests when Docker is available.
 */
@Disabled("Requires Docker - enable when Docker is available")
@DataMongoTest
@Testcontainers
class MongoDBIntegrationTest {

    companion object {
        @Container
        val mongoDBContainer = MongoDBContainer("mongo:7.0").apply {
            withExposedPorts(27017)
        }

        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri") { mongoDBContainer.replicaSetUrl }
        }
    }

    @Autowired
    private lateinit var personRepository: PersonRepository

    @BeforeEach
    fun setUp() {
        personRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        personRepository.deleteAll()
    }

    @Test
    fun `should save and retrieve person from MongoDB`() {
        // Given
        val person = PersonDocument(
            numericId = 1L,
            name = "John Doe",
            jobTitle = "Software Engineer",
            hobbies = listOf("coding", "hiking"),
            bio = "A passionate developer",
            location = Location(referenceId = 0L, latitude = 40.7128, longitude = -74.0060).toGeo()
        )

        // When
        val saved = personRepository.save(person)
        val retrieved = personRepository.findById(saved.id!!)

        // Then
        assertTrue(retrieved.isPresent)
        assertEquals("John Doe", retrieved.get().name)
        assertEquals("Software Engineer", retrieved.get().jobTitle)
        assertNotNull(retrieved.get().location)
    }

    @Test
    fun `should find persons near a location`() {
        // Given - Create persons at different locations
        val person1 = PersonDocument(
            numericId = 2L,
            name = "Alice",
            jobTitle = "Designer",
            hobbies = listOf("art"),
            bio = "Creative designer",
            location = Location(referenceId = 0L, latitude = 40.7128, longitude = -74.0060).toGeo() // NYC
        )

        val person2 = PersonDocument(
            numericId = 3L,
            name = "Bob",
            jobTitle = "Engineer",
            hobbies = listOf("coding"),
            bio = "Software engineer",
            location = Location(referenceId = 0L, latitude = 40.7589, longitude = -73.9851).toGeo() // Times Square (close)
        )

        val person3 = PersonDocument(
            numericId = 4L,
            name = "Charlie",
            jobTitle = "Teacher",
            hobbies = listOf("reading"),
            bio = "Passionate teacher",
            location = Location(referenceId = 0L, latitude = 34.0522, longitude = -118.2437).toGeo() // LA (far)
        )

        personRepository.saveAll(listOf(person1, person2, person3))

        // When - Search near NYC with 10km radius
        val center = Point(-74.0060, 40.7128) // MongoDB uses [longitude, latitude]
        val distance = Distance(10.0, Metrics.KILOMETERS)
        val nearbyPersons = personRepository.findByLocationNear(center, distance)

        // Then
        assertEquals(2, nearbyPersons.size, "Should find 2 persons within 10km")
        assertTrue(nearbyPersons.any { it.name == "Alice" })
        assertTrue(nearbyPersons.any { it.name == "Bob" })
        assertTrue(nearbyPersons.none { it.name == "Charlie" })
    }

    @Test
    fun `should handle multiple hobbies`() {
        // Given
        val person = PersonDocument(
            numericId = 5L,
            name = "Multi-talented Person",
            jobTitle = "Full Stack Developer",
            hobbies = listOf("coding", "music", "sports", "cooking"),
            bio = "Jack of all trades",
            location = null
        )

        // When
        val saved = personRepository.save(person)
        val retrieved = personRepository.findById(saved.id!!)

        // Then
        assertTrue(retrieved.isPresent)
        assertEquals(4, retrieved.get().hobbies.size)
        assertTrue(retrieved.get().hobbies.containsAll(listOf("coding", "music", "sports", "cooking")))
    }

    @Test
    fun `should update person location`() {
        // Given
        val person = PersonDocument(
            numericId = 6L,
            name = "Mobile Person",
            jobTitle = "Travel Blogger",
            hobbies = listOf("travel"),
            bio = "Always on the move",
            location = Location(referenceId = 0L, latitude = 40.7128, longitude = -74.0060).toGeo()
        )

        val saved = personRepository.save(person)

        // When - Update location
        val updated = saved.copy(
            location = Location(referenceId = 0L, latitude = 51.5074, longitude = -0.1278).toGeo() // London
        )
        personRepository.save(updated)

        val retrieved = personRepository.findById(saved.id!!)

        // Then
        assertTrue(retrieved.isPresent)
        val retrievedLocation = retrieved.get().location!!
        assertEquals(51.5074, retrievedLocation.coordinates[1], 0.0001) // latitude
        assertEquals(-0.1278, retrievedLocation.coordinates[0], 0.0001) // longitude
    }

    @Test
    fun `should find all persons`() {
        // Given
        val persons = listOf(
            PersonDocument(
                numericId = 7L,
                name = "Person 1",
                jobTitle = "Job 1",
                hobbies = listOf("hobby1"),
                bio = "Bio 1"
            ),
            PersonDocument(
                numericId = 8L,
                name = "Person 2",
                jobTitle = "Job 2",
                hobbies = listOf("hobby2"),
                bio = "Bio 2"
            ),
            PersonDocument(
                numericId = 9L,
                name = "Person 3",
                jobTitle = "Job 3",
                hobbies = listOf("hobby3"),
                bio = "Bio 3"
            )
        )

        personRepository.saveAll(persons)

        // When
        val allPersons = personRepository.findAll()

        // Then
        assertEquals(3, allPersons.size)
    }
}
