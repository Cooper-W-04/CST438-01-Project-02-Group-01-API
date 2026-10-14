package com.example.controller

import com.example.CustomUserDetails
import com.example.User
import com.example.dto.UserDto
import com.example.mapper.UserMapper
import com.example.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class UserController(private val userRepository: UserRepository, private val userMapper: UserMapper) {

    @GetMapping("/api/v1/users/me")
    fun getOwnProfile(@AuthenticationPrincipal principal: CustomUserDetails) : ResponseEntity<UserDto> {
        val userId: Long = principal.getId()
        val user = userRepository.findByIdOrNull(userId)?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "User profile not found"
        )
        return ResponseEntity.ok(userMapper.toDto(user))
    }

    /** List all users- Require admin permission
     * Gets all users, maps userDtos for all users, and returns that response entity.
     */
    @GetMapping("api/v1/users")
    @PreAuthorize("hasAuthority('USER_ADMIN')")
    fun getAllUsers(@AuthenticationPrincipal principal: CustomUserDetails) : ResponseEntity<List<UserDto>> {
        val userDtos = userRepository.findAll()
            .map { userMapper.toDto(it) }
        return ResponseEntity.ok(userDtos)
    }
}