package com.persons.finder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// Main Spring Boot application entry point
@SpringBootApplication
class ApplicationStarter

// Application startup function - launches the Spring Boot application
fun main(args: Array<String>) {
	runApplication<ApplicationStarter>(*args)
}
