package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@SpringBootApplication
@RestController
class getOwnProfile(private val userRepository: UserRepository) {

    @GetMapping("/api/v1/users/me")
    fun getOwnProfile(@AuthenticationPrincipal principal: CustomUserDetails) : User {
        val userId: Long = principal.getId()
        return userRepository.findByIdOrNull(userId)?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "User profile not found"
        )
    }
}