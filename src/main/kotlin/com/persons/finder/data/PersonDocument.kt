package com.persons.finder.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed

/**
 * MongoDB document representing a person with location data
 */
@Document(collection = "persons")
data class PersonDocument(
    @Id
    val id: String? = null,
    @org.springframework.data.mongodb.core.index.Indexed(unique = true)
    val numericId: Long, // Stable numeric ID for API responses
    val name: String,
    val jobTitle: String,
    val hobbies: List<String>,
    val bio: String,
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    val location: LocationGeo? = null
)

/**
 * GeoJSON Point representation for MongoDB geospatial queries
 * MongoDB expects coordinates in [longitude, latitude] order
 */
data class LocationGeo(
    val type: String = "Point",
    val coordinates: List<Double> // [longitude, latitude]
) {
    companion object {
        fun from(latitude: Double, longitude: Double): LocationGeo {
            return LocationGeo(coordinates = listOf(longitude, latitude))
        }
    }

    fun toLocation(referenceId: Long = 0L): Location {
        return Location(
            referenceId = referenceId,
            latitude = coordinates[1],
            longitude = coordinates[0]
        )
    }
}

/**
 * Convert PersonDocument to domain Person model
 */
fun PersonDocument.toPerson(): Person {
    return Person(
        id = this.numericId,
        name = this.name,
        jobTitle = this.jobTitle,
        hobbies = this.hobbies,
        bio = this.bio,
        location = this.location?.toLocation(this.numericId)
    )
}

/**
 * Convert domain Location to LocationGeo
 */
fun Location.toGeo(): LocationGeo {
    return LocationGeo.from(this.latitude, this.longitude)
}
