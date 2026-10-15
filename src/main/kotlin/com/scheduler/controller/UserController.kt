/**
 * REST Controller responsible for handling HTTP requests and routing for user-related endpoints.
 */
package com.scheduler.controller

import com.scheduler.model.User
import com.scheduler.repository.UserRepository
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["*"])
class UserController(private val userRepository: UserRepository) {

    @GetMapping
    fun getAllUsers(): List<User> {
        // Queries the Supabase 'users' table via Spring Data JPA
        return userRepository.findAll()
    }
}
