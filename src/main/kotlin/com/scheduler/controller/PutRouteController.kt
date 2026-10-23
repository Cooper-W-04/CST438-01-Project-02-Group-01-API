package com.scheduler.controller

import com.scheduler.dto.PutGroupRequest
import com.scheduler.dto.PutGroupResponse
import com.scheduler.dto.PutMeResponse
import com.scheduler.security.CustomUserDetails
import com.scheduler.model.Group
import com.scheduler.service.GroupNotFoundException
import com.scheduler.service.ImproperTimesFormatException
import com.scheduler.service.PutRouteService
import com.scheduler.service.TimeSlotNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class PutRouteController(
    private val putRouteService: PutRouteService
){
    //Do the routing for the two puts 
    
    //replace everything except the stuff done at the start like id, created_at, created_by
    //And the stuff you're not allowed to touch since it's not yours
    @PutMapping("/api/v1/groups/{groupId}")
    fun replaceGroup(
        @PathVariable groupId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestBody groupRequest: PutGroupRequest,
    ): ResponseEntity<PutGroupResponse> {
       
       return try {  
           ResponseEntity.status(HttpStatus.CREATED).body(putRouteService.replaceGroup(groupId, principal.getId(), groupRequest.groupName, groupRequest.times))
       }
       catch (e: ImproperTimesFormatException){
          throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Improper Format of Times"
            )
       }
       catch (e: TimeSlotNotFoundException){
          throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Time Slot Not Found"
            )
       }
       catch (e: GroupNotFoundException){
          throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Group Doesn't Exist"
            )
       }
    }
    

    //replace all availabilities for a user in a group
    @PutMapping("/api/v1/groups/{groupId}/availability/me")
    fun replaceAvailability(
        @PathVariable groupId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestBody times: String,
    ): ResponseEntity<PutMeResponse> {
        //Get user id through getOwnProfile to get the userID
        val userId = principal.getId()
       
       return try {
           ResponseEntity.status(HttpStatus.CREATED).body(putRouteService.replaceUserAvailability(groupId, userId, times))
       }catch (e: ImproperTimesFormatException){
          throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Improper Format of Times"
            )
       }
       catch (e: TimeSlotNotFoundException){
          throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Time Slot Not Found"
            )
       }
    }


}