package com.example.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

// Optional: A simple inline controller to verify your API works immediately
@RestController
@RequestMapping("/api/v1")
class HelloController {

    @GetMapping("/hello")
    fun sayHello(): Map<String, String> {
        return mapOf("message" to "Hello World from Spring Boot & Kotlin!")
    }
}
