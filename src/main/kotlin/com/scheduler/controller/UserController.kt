/**
 * REST Controller responsible for handling HTTP requests and routing for user-related endpoints.
 */
package com.scheduler.controller

import com.scheduler.dto.UserDto
import com.scheduler.mapper.UserMapper
import com.scheduler.model.User
import com.scheduler.repository.UserRepository
import com.scheduler.security.CustomUserDetails
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class UserController(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {
    @GetMapping("/api/v1/users/me")
    fun getOwnProfile(
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<UserDto> {
        val userId: Long = principal.getId()

        val user = userRepository.findByIdOrNull(userId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User profile not found"
            )

        return ResponseEntity.ok(userMapper.toDto(user))
    }

    /**
     * List all users - Require admin permission.
     * Gets all users, maps userDtos for all users, and returns that response entity.
     */
    @GetMapping("/api/v1/users")
    @PreAuthorize("hasAuthority('USER_ADMIN')")
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        val userDtos = userRepository.findAll()
            .map { userMapper.toDto(it) }

        return ResponseEntity.ok(userDtos)
    }

    /**
     * View one user by userID.
     */
    @GetMapping("/api/v1/users/{userId}")
    @PreAuthorize("hasAuthority('USER_ADMIN')")
    fun getUserById(
        @PathVariable userId: Long
    ): ResponseEntity<UserDto> {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User profile not found"
            )

        return ResponseEntity.ok(userMapper.toDto(user))
    }
}