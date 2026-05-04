package com.persons.finder.domain.services

/**
 * Service for detecting and preventing prompt injection attacks
 */
interface PromptInjectionService {
    /**
     * Check if the input contains prompt injection attempts
     * @param input The user input to check
     * @return true if injection detected, false otherwise
     */
    suspend fun detectInjection(input: String): Boolean

    /**
     * Sanitize input to remove potentially dangerous content
     * @param input The user input to sanitize
     * @return Sanitized string
     */
    fun sanitizeInput(input: String): String
}

/**
 * Result of prompt injection detection
 */
data class InjectionDetectionResult(
    val isInjection: Boolean,
    val confidence: Double,
    val reason: String
)
