package com.persons.finder.config

import com.persons.finder.data.LocationGeo
import com.persons.finder.data.PersonDocument
import com.persons.finder.data.PersonRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * Data seeding configuration for MongoDB
 * Seeds database with sample data for testing and benchmarking
 */
@Configuration
class DataSeeder {

    companion object {
        // Sample data pools
        private val firstNames = listOf(
            "Alice", "Bob", "Charlie", "Diana", "Edward", "Fiona", "George", "Hannah",
            "Isaac", "Julia", "Kevin", "Laura", "Michael", "Nancy", "Oliver", "Patricia",
            "Quinn", "Rachel", "Samuel", "Teresa", "Uma", "Victor", "Wendy", "Xavier",
            "Yolanda", "Zachary", "Emma", "Liam", "Olivia", "Noah", "Ava", "Ethan",
            "Sophia", "Mason", "Isabella", "William", "Mia", "James", "Charlotte", "Benjamin"
        )

        private val lastNames = listOf(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
            "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson",
            "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Walker", "Hall",
            "Allen", "Young", "King", "Wright", "Scott", "Green", "Baker", "Adams"
        )

        private val jobTitles = listOf(
            "Software Engineer", "Data Scientist", "Product Manager", "UX Designer",
            "Marketing Manager", "Sales Representative", "Accountant", "Teacher",
            "Nurse", "Doctor", "Lawyer", "Chef", "Architect", "Photographer",
            "Writer", "Artist", "Musician", "Consultant", "Analyst", "Developer",
            "DevOps Engineer", "ML Engineer", "Business Analyst", "Project Manager"
        )

        private val hobbiesList = listOf(
            "hiking", "cooking", "reading", "gaming", "photography", "traveling",
            "yoga", "painting", "gardening", "cycling", "swimming", "running",
            "dancing", "singing", "writing", "coding", "chess", "pottery",
            "knitting", "baking", "meditation", "rock climbing", "surfing", "skiing"
        )

        // World major cities coordinates for realistic location distribution
        private val cityLocations = listOf(
            Pair(40.7128, -74.0060),  // New York
            Pair(51.5074, -0.1278),   // London
            Pair(35.6762, 139.6503),  // Tokyo
            Pair(48.8566, 2.3522),    // Paris
            Pair(-33.8688, 151.2093), // Sydney
            Pair(37.7749, -122.4194), // San Francisco
            Pair(52.5200, 13.4050),   // Berlin
            Pair(55.7558, 37.6173),   // Moscow
            Pair(-23.5505, -46.6333), // São Paulo
            Pair(19.4326, -99.1332),  // Mexico City
            Pair(1.3521, 103.8198),   // Singapore
            Pair(39.9042, 116.4074),  // Beijing
            Pair(28.6139, 77.2090),   // Delhi
            Pair(-26.2041, 28.0473),  // Johannesburg
            Pair(43.6532, -79.3832)   // Toronto
        )
    }

    /**
     * Seeds the database with sample persons
     * Only runs when 'seed' profile is active
     * Usage: ./gradlew bootRun --args='--spring.profiles.active=seed'
     */
    @Bean
    @Profile("seed")
    fun seedDatabase(personRepository: PersonRepository) = CommandLineRunner {
        val count = personRepository.count()

        if (count > 0) {
            println("⚠️  Database already contains $count records. Skipping seed.")
            println("   To force reseed, run: docker-compose down -v && docker-compose up -d")
            return@CommandLineRunner
        }

        println("🌱 Starting database seeding...")
        val startTime = System.currentTimeMillis()

        val recordsToSeed = System.getenv("SEED_COUNT")?.toIntOrNull() ?: 10000
        val batchSize = 1000

        var totalSeeded = 0

        for (batch in 0 until recordsToSeed / batchSize) {
            val persons = mutableListOf<PersonDocument>()

            for (i in 0 until batchSize) {
                val person = generateRandomPerson()
                persons.add(person)
            }

            personRepository.saveAll(persons)
            totalSeeded += persons.size

            val progress = (totalSeeded * 100) / recordsToSeed
            print("\r   Progress: $progress% ($totalSeeded/$recordsToSeed)")
        }

        val elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0
        println()
        println("✅ Database seeded successfully!")
        println("   Total records: $totalSeeded")
        println("   Time taken: ${"%.2f".format(elapsedTime)}s")
        println("   Records per second: ${"%.0f".format(totalSeeded / elapsedTime)}")
    }

    /**
     * Generates a random person with realistic data
     */
    private fun generateRandomPerson(): PersonDocument {
        val name = "${firstNames.random()} ${lastNames.random()}"
        val jobTitle = jobTitles.random()
        val hobbies = hobbiesList.shuffled().take(Random.nextInt(2, 5))
        val bio = generateBio(jobTitle, hobbies)

        // Pick a random city and add small random offset (within ~10km)
        val baseCity = cityLocations.random()
        val latOffset = Random.nextDouble(-0.1, 0.1)  // ~11km at equator
        val lonOffset = Random.nextDouble(-0.1, 0.1)
        val location = LocationGeo.from(
            latitude = baseCity.first + latOffset,
            longitude = baseCity.second + lonOffset
        )

        return PersonDocument(
            id = null,
            name = name,
            jobTitle = jobTitle,
            hobbies = hobbies,
            bio = bio,
            location = location
        )
    }

    /**
     * Generates a simple bio without calling OpenAI
     * Used for seeding to avoid API costs
     */
    private fun generateBio(jobTitle: String, hobbies: List<String>): String {
        val templates = listOf(
            "Meet $jobTitle who loves ${hobbies.joinToString(" and ")}.",
            "A passionate $jobTitle with interests in ${hobbies.joinToString(", ")}.",
            "$jobTitle by day, ${hobbies.first()} enthusiast by night!",
            "This $jobTitle balances ${hobbies.joinToString(" and ")} with a thriving career."
        )

        val templateIndex = (jobTitle.hashCode() + hobbies.hashCode()).absoluteValue % templates.size
        return templates[templateIndex].replace("$jobTitle", jobTitle)
    }
}
