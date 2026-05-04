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

@RestController
@RequestMapping("api/v1/persons")
class PersonController @Autowired constructor(
    private val personsService: PersonsService,
    private val locationsService: LocationsService,
    private val aiBioService: AIBioService
) {

    @PostMapping
    fun createPerson(@RequestBody request: CreatePersonRequest): ResponseEntity<CreatePersonResponse> {
        val bio = aiBioService.generateBio(request.jobTitle, request.hobbies)

        val location = Location(
            referenceId = 0,
            latitude = request.latitude,
            longitude = request.longitude
        )

        val person = Person(
            id = 0,
            name = request.name,
            jobTitle = request.jobTitle,
            hobbies = request.hobbies,
            bio = bio,
            location = location
        )

        val savedPerson = personsService.save(person)

        locationsService.addLocation(
            location.copy(referenceId = savedPerson.id)
        )

        val response = CreatePersonResponse(
            id = savedPerson.id,
            name = savedPerson.name,
            jobTitle = savedPerson.jobTitle,
            hobbies = savedPerson.hobbies,
            bio = savedPerson.bio
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}/location")
    fun updateLocation(
        @PathVariable id: Long,
        @RequestBody request: UpdateLocationRequest
    ): ResponseEntity<Any> {
        val person = personsService.getById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Person not found"))

        val newLocation = Location(
            referenceId = id,
            latitude = request.latitude,
            longitude = request.longitude
        )

        locationsService.addLocation(newLocation)

        person.location = newLocation
        personsService.save(person)

        return ResponseEntity.ok(mapOf("message" to "Location updated successfully"))
    }

    @GetMapping("/nearby")
    fun getNearbyPersons(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(defaultValue = "10.0") radius: Double
    ): ResponseEntity<List<PersonNearbyResponse>> {
        val nearbyLocations = locationsService.findAround(latitude, longitude, radius)

        val nearbyPersons = nearbyLocations.mapNotNull { location ->
            val person = personsService.getById(location.referenceId)
            person?.let {
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

    @GetMapping("")
    fun getExample(): String {
        return "Hello Example"
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1.0 - a))

        return earthRadiusKm * c
    }
}