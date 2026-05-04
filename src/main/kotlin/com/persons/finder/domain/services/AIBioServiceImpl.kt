package com.persons.finder.domain.services

import org.springframework.stereotype.Service

@Service
class AIBioServiceImpl : AIBioService {

    override fun generateBio(jobTitle: String, hobbies: List<String>): String {
        val sanitizedJobTitle = sanitizeInput(jobTitle)
        val sanitizedHobbies = hobbies.map { sanitizeInput(it) }

        return mockAIGeneration(sanitizedJobTitle, sanitizedHobbies)
    }

    private fun sanitizeInput(input: String): String {
        val maxLength = 100
        val trimmed = input.trim().take(maxLength)

        val forbiddenPhrases = listOf(
            "ignore",
            "disregard",
            "forget",
            "system",
            "prompt",
            "instruction",
            "override",
            "hack"
        )

        val lowerInput = trimmed.lowercase()
        if (forbiddenPhrases.any { lowerInput.contains(it) }) {
            return trimmed.replace(Regex("[^a-zA-Z0-9\\s,.-]"), "")
        }

        return trimmed.replace(Regex("[^a-zA-Z0-9\\s,.-]"), "")
    }

    private fun mockAIGeneration(jobTitle: String, hobbies: List<String>): String {
        val hobbiesText = when {
            hobbies.isEmpty() -> "various interests"
            hobbies.size == 1 -> hobbies[0]
            hobbies.size == 2 -> "${hobbies[0]} and ${hobbies[1]}"
            else -> "${hobbies.dropLast(1).joinToString(", ")}, and ${hobbies.last()}"
        }

        val templates = listOf(
            "Meet a $jobTitle who lives for $hobbiesText! They bring creative energy to everything they do.",
            "This $jobTitle somehow balances $hobbiesText and a thriving career. Impressive multitasker!",
            "A passionate $jobTitle with a love for $hobbiesText. Always up for new adventures!",
            "By day: $jobTitle. By night: enthusiast of $hobbiesText. Double life champion!",
            "$jobTitle extraordinaire who geeks out over $hobbiesText. Interesting combination, right?"
        )

        val seed = (jobTitle + hobbies.joinToString()).hashCode()
        return templates[Math.abs(seed) % templates.size]
    }
}
