package com.persons.finder

import com.persons.finder.data.Location
import com.persons.finder.domain.services.LocationsServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LocationsServiceTest {

    private lateinit var locationsService: LocationsServiceImpl

    @BeforeEach
    fun setup() {
        locationsService = LocationsServiceImpl()
    }

    @Test
    fun `should add location`() {
        val location = Location(referenceId = 1, latitude = 40.7128, longitude = -74.0060)

        locationsService.addLocation(location)

        val nearby = locationsService.findAround(40.7128, -74.0060, 1.0)
        assertEquals(1, nearby.size)
        assertEquals(1, nearby[0].referenceId)
    }

    @Test
    fun `should find locations within radius`() {
        locationsService.addLocation(Location(1, 40.7128, -74.0060)) // NYC
        locationsService.addLocation(Location(2, 40.7589, -73.9851)) // Near NYC (Times Square)
        locationsService.addLocation(Location(3, 34.0522, -118.2437)) // LA (far away)

        val nearby = locationsService.findAround(40.7128, -74.0060, 10.0)

        assertEquals(2, nearby.size)
        assertTrue(nearby.any { it.referenceId == 1L })
        assertTrue(nearby.any { it.referenceId == 2L })
        assertFalse(nearby.any { it.referenceId == 3L })
    }

    @Test
    fun `should sort locations by distance`() {
        val centerLat = 40.7128
        val centerLon = -74.0060

        locationsService.addLocation(Location(1, 40.7589, -73.9851)) // ~7km away
        locationsService.addLocation(Location(2, 40.7128, -74.0060)) // 0km away (same location)
        locationsService.addLocation(Location(3, 40.7300, -74.0000)) // ~2km away

        val nearby = locationsService.findAround(centerLat, centerLon, 20.0)

        assertEquals(3, nearby.size)
        assertEquals(2, nearby[0].referenceId, "Closest location should be first")
        assertEquals(3, nearby[1].referenceId, "Second closest should be second")
        assertEquals(1, nearby[2].referenceId, "Farthest should be last")
    }

    @Test
    fun `should remove location`() {
        locationsService.addLocation(Location(1, 40.7128, -74.0060))
        locationsService.addLocation(Location(2, 40.7589, -73.9851))

        locationsService.removeLocation(1)

        val nearby = locationsService.findAround(40.7128, -74.0060, 100.0)
        assertEquals(1, nearby.size)
        assertEquals(2, nearby[0].referenceId)
    }

    @Test
    fun `should return empty list when no locations within radius`() {
        locationsService.addLocation(Location(1, 34.0522, -118.2437)) // LA

        val nearby = locationsService.findAround(40.7128, -74.0060, 10.0) // NYC

        assertTrue(nearby.isEmpty())
    }

    @Test
    fun `should handle multiple locations at same coordinates`() {
        locationsService.addLocation(Location(1, 40.7128, -74.0060))
        locationsService.addLocation(Location(2, 40.7128, -74.0060))

        val nearby = locationsService.findAround(40.7128, -74.0060, 1.0)

        assertEquals(2, nearby.size)
    }
}
