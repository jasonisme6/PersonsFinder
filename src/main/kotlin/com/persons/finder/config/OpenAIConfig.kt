package com.persons.finder.config

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.seconds

/**
 * Configuration for OpenAI API client
 */
@Configuration
class OpenAIConfig {

    @Value("\${openai.api.key}")
    private lateinit var apiKey: String

    @Bean
    fun openAI(): OpenAI {
        return OpenAI(
            token = apiKey,
            timeout = Timeout(socket = 60.seconds)
        )
    }
}
