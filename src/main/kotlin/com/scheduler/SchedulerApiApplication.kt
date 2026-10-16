/**
 * Main entry point for the Schedulr Spring Boot application.
 * Bootstraps and launches the embedded web server and application context.
 */
package com.scheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SchedulrApiApplication

fun main(args: Array<String>) {
    runApplication<SchedulrApiApplication>(*args)
}
