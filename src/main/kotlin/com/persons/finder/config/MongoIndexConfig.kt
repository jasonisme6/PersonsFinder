package com.persons.finder.config

import com.persons.finder.data.PersonDocument
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.GeospatialIndex
import org.springframework.data.mongodb.core.index.Index
import javax.annotation.PostConstruct

/**
 * MongoDB index configuration
 * Creates required indexes for optimal query performance
 */
@Configuration
class MongoIndexConfig(
    private val mongoTemplate: MongoTemplate
) {

    @PostConstruct
    fun initIndexes() {
        try {
            // Drop the entire collection to remove corrupted data
            mongoTemplate.dropCollection(PersonDocument::class.java)
            println("🗑️  Dropped existing persons collection")
        } catch (e: Exception) {
            println("⚠️  Could not drop collection: ${e.message}")
        }

        val indexOps = mongoTemplate.indexOps(PersonDocument::class.java)

        // Geospatial 2dsphere index for location-based queries
        // Required for efficient nearby person searches
        val geospatialIndex = GeospatialIndex("location")
            .typed(org.springframework.data.mongodb.core.index.GeoSpatialIndexType.GEO_2DSPHERE)

        indexOps.ensureIndex(geospatialIndex)

        // Unique index on numericId for efficient lookups
        val numericIdIndex = Index()
            .on("numericId", Sort.Direction.ASC)
            .unique()

        indexOps.ensureIndex(numericIdIndex)

        // Index on name for search queries
        val nameIndex = Index()
            .on("name", Sort.Direction.ASC)

        indexOps.ensureIndex(nameIndex)

        println("✅ MongoDB indexes created successfully")
        println("   - Geospatial 2dsphere index on 'location'")
        println("   - Unique index on 'numericId'")
        println("   - Ascending index on 'name'")
    }
}
