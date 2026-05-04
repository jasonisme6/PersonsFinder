package com.persons.finder.domain.services

import org.springframework.stereotype.Service

@Service
class AIBioServiceImpl : AIBioService {

    // Generates a biography by sanitizing inputs and creating a template-based bio
    override fun generateBio(jobTitle: String, hobbies: List<String>): String {
        val sanitizedJobTitle = sanitizeInput(jobTitle)
        val sanitizedHobbies = hobbies.map { sanitizeInput(it) }

        return mockAIGeneration(sanitizedJobTitle, sanitizedHobbies)
    }

    // Sanitizes user input to prevent prompt injection attacks
    // Limits length, detects malicious keywords, and strips special characters
    private fun sanitizeInput(input: String): String {
        // Limit input to 100 characters
        val maxLength = 100
        val trimmed = input.trim().take(maxLength)

        // List of keywords commonly used in prompt injection attacks
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

        // Check for forbidden phrases (case-insensitive)
        val lowerInput = trimmed.lowercase()
        if (forbiddenPhrases.any { lowerInput.contains(it) }) {
            // Strip all special characters, keeping only alphanumeric, spaces, and basic punctuation
            return trimmed.replace(Regex("[^a-zA-Z0-9\\s,.-]"), "")
        }

        // Always strip special characters regardless of detection
        return trimmed.replace(Regex("[^a-zA-Z0-9\\s,.-]"), "")
    }

    // Generates a mock AI biography using predefined templates
    // Uses deterministic randomization based on input hash for consistency
    private fun mockAIGeneration(jobTitle: String, hobbies: List<String>): String {
        // Format hobbies list into natural language
        val hobbiesText = when {
            hobbies.isEmpty() -> "various interests"
            hobbies.size == 1 -> hobbies[0]
            hobbies.size == 2 -> "${hobbies[0]} and ${hobbies[1]}"
            else -> "${hobbies.dropLast(1).joinToString(", ")}, and ${hobbies.last()}"
        }

        // Predefined bio templates with personality
        val templates = listOf(
            "Meet a $jobTitle who lives for $hobbiesText! They bring creative energy to everything they do.",
            "This $jobTitle somehow balances $hobbiesText and a thriving career. Impressive multitasker!",
            "A passionate $jobTitle with a love for $hobbiesText. Always up for new adventures!",
            "By day: $jobTitle. By night: enthusiast of $hobbiesText. Double life champion!",
            "$jobTitle extraordinaire who geeks out over $hobbiesText. Interesting combination, right?"
        )

        // Use hash of inputs to deterministically select a template
        val seed = (jobTitle + hobbies.joinToString()).hashCode()
        return templates[Math.abs(seed) % templates.size]
    }
}
