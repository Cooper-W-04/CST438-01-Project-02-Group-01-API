package com.scheduler.controller

import com.scheduler.dto.PatchGroupRequest
import com.scheduler.security.CustomUserDetails
import com.scheduler.service.PatchRoutesService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class PatchRoutesController(
    private val patchRoutesService: PatchRoutesService
) {
    @PatchMapping("/api/v1/groups/{groupId}")
    fun patchGroup(
        @PathVariable groupId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestBody request: PatchGroupRequest
    ): ResponseEntity<Void> {
        try {
            val updated = patchRoutesService.patchGroupEventName(
                groupId = groupId,
                userId = principal.getId(),
                eventName = request.eventName
            )

            if (!updated) {
                throw ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Group not found or user is not the group creator"
                )
            }

            return ResponseEntity.noContent().build()
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                exception.message
            )
        }
    }
}