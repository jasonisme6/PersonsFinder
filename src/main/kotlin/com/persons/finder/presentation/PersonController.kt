package com.persons.finder.presentation

import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.domain.services.AIBioService
import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.math.*

// REST controller for person-related endpoints
@RestController
@RequestMapping("api/v1/persons")
class PersonController @Autowired constructor(
    private val personsService: PersonsService,
    private val locationsService: LocationsService,
    private val aiBioService: AIBioService
) {

    // POST /api/v1/persons - Creates a new person with initial location
    @PostMapping
    fun createPerson(@RequestBody request: CreatePersonRequest): ResponseEntity<CreatePersonResponse> {
        // Generate AI biography based on job title and hobbies
        val bio = aiBioService.generateBio(request.jobTitle, request.hobbies)

        // Create location object with temporary reference ID
        val location = Location(
            referenceId = 0,
            latitude = request.latitude,
            longitude = request.longitude
        )

        // Create person object with temporary ID
        val person = Person(
            id = 0,
            name = request.name,
            jobTitle = request.jobTitle,
            hobbies = request.hobbies,
            bio = bio,
            location = location
        )

        // Save person and get the generated ID
        val savedPerson = personsService.save(person)

        // Add location with actual person ID
        locationsService.addLocation(
            location.copy(referenceId = savedPerson.id)
        )

        // Build response DTO
        val response = CreatePersonResponse(
            id = savedPerson.id,
            name = savedPerson.name,
            jobTitle = savedPerson.jobTitle,
            hobbies = savedPerson.hobbies,
            bio = savedPerson.bio
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // PUT /api/v1/persons/{id}/location - Updates a person's location
    @PutMapping("/{id}/location")
    fun updateLocation(
        @PathVariable id: Long,
        @RequestBody request: UpdateLocationRequest
    ): ResponseEntity<Any> {
        // Check if person exists
        val person = personsService.getById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Person not found"))

        // Create new location with updated coordinates
        val newLocation = Location(
            referenceId = id,
            latitude = request.latitude,
            longitude = request.longitude
        )

        // Update location in location service
        locationsService.addLocation(newLocation)

        // Update person's location reference
        person.location = newLocation
        personsService.save(person)

        return ResponseEntity.ok(mapOf("message" to "Location updated successfully"))
    }

    // GET /api/v1/persons/nearby - Finds persons within a given radius from coordinates
    @GetMapping("/nearby")
    fun getNearbyPersons(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(defaultValue = "10.0") radius: Double
    ): ResponseEntity<List<PersonNearbyResponse>> {
        // Find all locations within the specified radius
        val nearbyLocations = locationsService.findAround(latitude, longitude, radius)

        // Map locations to person response objects with distance
        val nearbyPersons = nearbyLocations.mapNotNull { location ->
            val person = personsService.getById(location.referenceId)
            person?.let {
                // Calculate exact distance for this person
                val distance = calculateDistance(
                    latitude, longitude,
                    location.latitude, location.longitude
                )
                PersonNearbyResponse(
                    id = it.id,
                    name = it.name,
                    jobTitle = it.jobTitle,
                    hobbies = it.hobbies,
                    bio = it.bio,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    distanceInKm = distance
                )
            }
        }.sortedBy { it.distanceInKm }

        return ResponseEntity.ok(nearbyPersons)
    }

    // GET /api/v1/persons - Simple test endpoint
    @GetMapping("")
    fun getExample(): String {
        return "Hello Example"
    }

    // Calculates the great-circle distance between two points on Earth using the Haversine formula
    // Returns distance in kilometers
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // Earth's radius in kilometers
        val earthRadiusKm = 6371.0

        // Convert latitude and longitude differences to radians
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        // Haversine formula: calculates the shortest distance over the earth's surface
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        // Calculate the angular distance in radians
        val c = 2 * atan2(sqrt(a), sqrt(1.0 - a))

        // Convert to linear distance
        return earthRadiusKm * c
    }
}