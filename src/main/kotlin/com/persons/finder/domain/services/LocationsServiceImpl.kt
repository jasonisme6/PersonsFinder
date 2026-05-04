package com.persons.finder.domain.services

import com.persons.finder.data.Location
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*

@Service
class LocationsServiceImpl : LocationsService {

    private val locations = ConcurrentHashMap<Long, Location>()

    override fun addLocation(location: Location) {
        locations[location.referenceId] = location
    }

    override fun removeLocation(locationReferenceId: Long) {
        locations.remove(locationReferenceId)
    }

    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {
        return locations.values
            .map { location ->
                val distance = calculateDistance(latitude, longitude, location.latitude, location.longitude)
                location to distance
            }
            .filter { (_, distance) -> distance <= radiusInKm }
            .sortedBy { (_, distance) -> distance }
            .map { (location, _) -> location }
    }

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