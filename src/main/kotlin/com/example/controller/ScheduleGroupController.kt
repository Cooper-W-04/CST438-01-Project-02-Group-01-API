package com.example.controller

import com.example.CustomUserDetails
import com.example.dto.GroupDto
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable

@RestController
class ScheduleGroupController {
    /** View one scheduling group by groupId
     *
     */
    //@GetMapping("/api/v1/groups/{groupId}")
    //May need to verify using the authenticationprincipal that the
    /* fun getGroupById(@PathVariable groupId: Long, @AuthenticationPrincipal principal: CustomUserDetails) : ResponseEntity<GroupDto> {
        //TODO
    } */
}