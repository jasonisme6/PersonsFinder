package com.persons.finder.domain.services

import com.persons.finder.data.Location
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*

@Service
class LocationsServiceImpl : LocationsService {

    // Thread-safe in-memory storage for locations, keyed by reference ID
    private val locations = ConcurrentHashMap<Long, Location>()

    // Stores or updates a location
    override fun addLocation(location: Location) {
        locations[location.referenceId] = location
    }

    // Removes a location from storage
    override fun removeLocation(locationReferenceId: Long) {
        locations.remove(locationReferenceId)
    }

    // Finds all locations within a specified radius and returns them sorted by distance
    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {
        return locations.values
            // Calculate distance for each location
            .map { location ->
                val distance = calculateDistance(latitude, longitude, location.latitude, location.longitude)
                location to distance
            }
            // Keep only locations within the radius
            .filter { (_, distance) -> distance <= radiusInKm }
            // Sort by distance (nearest first)
            .sortedBy { (_, distance) -> distance }
            // Extract just the locations (drop distance)
            .map { (location, _) -> location }
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
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        // Calculate the angular distance in radians
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        // Convert to linear distance
        return earthRadiusKm * c
    }
}