package com.persons.finder.config

import io.github.cdimascio.dotenv.dotenv
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource

/**
 * Loads environment variables from .env file into Spring's environment
 */
class DotenvConfig : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val dotenv = dotenv {
            ignoreIfMissing = true
            systemProperties = false
        }

        val dotenvMap = dotenv.entries().associate { it.key to it.value }
        val propertySource = MapPropertySource("dotenvProperties", dotenvMap)

        applicationContext.environment.propertySources.addFirst(propertySource)
    }
}
