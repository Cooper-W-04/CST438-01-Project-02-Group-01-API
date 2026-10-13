package com.example

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class DeleteRoutesController(
    private val deleteRoutesService: DeleteRoutesService
) {
    @DeleteMapping("/api/v1/users/me")
    fun deleteOwnAccount(
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<Void> {
        val deleted = deleteRoutesService.deleteOwnAccount(principal.getId())

        if (!deleted) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User account not found"
            )
        }

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/v1/groups/{groupId}/members/me")
    fun leaveGroup(
        @PathVariable groupId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<Void> {
        val deleted = deleteRoutesService.leaveGroup(
            groupId,
            principal.getId()
        )

        if (!deleted) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Group membership not found"
            )
        }

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/v1/availability/{availabilityId}")
    fun deleteAvailability(
        @PathVariable availabilityId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<Void> {
        val deleted = deleteRoutesService.deleteAvailability(
            availabilityId,
            principal.getId()
        )

        if (!deleted) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Availability record not found"
            )
        }

        return ResponseEntity.noContent().build()
    }
}