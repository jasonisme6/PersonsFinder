package com.persons.finder.domain.services

import com.persons.finder.data.Location

// Service interface for managing geographic locations
interface LocationsService {
    // Adds or updates a location in the system
    fun addLocation(location: Location)
    // Removes a location by its reference ID
    fun removeLocation(locationReferenceId: Long)
    // Finds all locations within a given radius from a coordinate point, sorted by distance
    fun findAround(latitude: Double, longitude: Double, radiusInKm: Double) : List<Location>
}