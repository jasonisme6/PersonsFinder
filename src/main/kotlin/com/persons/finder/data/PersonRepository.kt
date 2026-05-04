package com.persons.finder.data

import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * MongoDB repository for Person documents
 * Provides geospatial query support for finding nearby persons
 */
@Repository
interface PersonRepository : MongoRepository<PersonDocument, String> {

    /**
     * Find a person by numeric ID
     */
    fun findByNumericId(numericId: Long): PersonDocument?

    /**
     * Find all persons within a specified distance from a point
     * @param point Center point (longitude, latitude)
     * @param distance Maximum distance
     * @return List of persons within the distance
     */
    fun findByLocationNear(point: Point, distance: Distance): List<PersonDocument>
}
