package com.scheduler.controller

import com.scheduler.dto.CreateGroupRequest
import com.scheduler.dto.GroupMembershipResponse
import com.scheduler.dto.GroupResponse
import com.scheduler.security.CustomUserDetails
import com.scheduler.service.AlreadyMemberException
import com.scheduler.service.GroupNotFoundException
import com.scheduler.service.PostRoutesService
import com.scheduler.service.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class PostRoutesController(
    private val postRoutesService: PostRoutesService
) {
    @PostMapping("/api/v1/groups")
    fun createGroup(
        @RequestBody request: CreateGroupRequest,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<GroupResponse> {
        return try {
            val group = postRoutesService.createGroup(
                eventName = request.eventName,
                userId = principal.getId()
            )

            ResponseEntity.status(HttpStatus.CREATED).body(group)
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                exception.message
            )
        } catch (_: UserNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Authenticated user was not found"
            )
        }
    }

    @PostMapping("/api/v1/groups/{groupId}/members")
    fun joinGroup(
        @PathVariable groupId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<GroupMembershipResponse> {
        return try {
            val membership = postRoutesService.joinGroup(
                groupId = groupId,
                userId = principal.getId()
            )

            ResponseEntity.status(HttpStatus.CREATED).body(membership)
        } catch (_: UserNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Authenticated user was not found"
            )
        } catch (_: GroupNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Group was not found"
            )
        } catch (_: AlreadyMemberException) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "User is already a member of this group"
            )
        }
    }
}