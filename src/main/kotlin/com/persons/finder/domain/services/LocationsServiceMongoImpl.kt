package com.persons.finder.domain.services

import com.persons.finder.data.Location
import com.persons.finder.data.PersonRepository
import com.persons.finder.data.toGeo
import org.springframework.context.annotation.Primary
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service
import kotlin.math.*

/**
 * MongoDB-backed LocationsService using geospatial queries
 */
@Service
@Primary
class LocationsServiceMongoImpl(
    private val personRepository: PersonRepository
) : LocationsService {

    override fun addLocation(location: Location) {
        // Location updates are handled through PersonsService
        // This method is kept for interface compatibility
    }

    override fun removeLocation(locationReferenceId: Long) {
        // Location removal is handled through PersonsService
        // This method is kept for interface compatibility
    }

    /**
     * Find locations around a point using MongoDB's geospatial query
     */
    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {
        val center = Point(longitude, latitude) // MongoDB uses [longitude, latitude]
        val distance = Distance(radiusInKm, Metrics.KILOMETERS)

        return personRepository.findByLocationNear(center, distance)
            .mapNotNull { personDoc ->
                personDoc.location?.let { locationGeo ->
                    locationGeo.toLocation(personDoc.numericId)
                }
            }
            .sortedBy { loc ->
                calculateDistance(latitude, longitude, loc.latitude, loc.longitude)
            }
    }

    /**
     * Haversine formula for calculating distance between two points
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }
}
