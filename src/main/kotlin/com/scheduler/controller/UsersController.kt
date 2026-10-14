/**
 * REST Controller responsible for handling HTTP requests and routing for user-related endpoints.
 */
package com.scheduler.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UsersController {

    @GetMapping
    fun getUsers(): List<String> {
        return listOf("User 1", "User 2")
    }
}
