package com.scheduler.controller

import com.scheduler.mapper.GroupMapper
import com.scheduler.repository.GroupRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import com.scheduler.dto.GroupDto
import com.scheduler.model.Member
import com.scheduler.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.server.ResponseStatusException

@RestController
class GroupController (
    private val groupRepository : GroupRepository,
    private val groupMapper : GroupMapper,
    private val memberRepository: MemberRepository,
) {
    //Get a group by groupId; Requires user auth, must be part of the group being checked
    @GetMapping("/api/v1/groups/{groupId}")
    @PreAuthorize(
        "hasRole('USER') and " +
                "@groupSecurity.isMemberOfGroup(#groupId, principal.getId())"
    )
    fun getGroupById(
        @PathVariable groupId: Long
    ): ResponseEntity<GroupDto> {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Group not found"
            )

        return ResponseEntity.ok(groupMapper.toDto(group))
    }

    @GetMapping("/api/v1/groups/{groupId}/members")
    @PreAuthorize(
        "hasRole('USER') and " +
                "@groupSecurity.isMemberOfGroup(#groupId, principal.getId())"
    )
    fun getMembersOfGroup(
        @PathVariable groupId: Long
    ): ResponseEntity<List<Member>> {
        if (!groupRepository.existsById(groupId)) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Group not found"
            )
        }
        val members = memberRepository.findAllByGroupId(groupId)
        return ResponseEntity.ok(members)
    }
}