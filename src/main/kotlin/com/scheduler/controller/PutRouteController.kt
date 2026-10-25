package com.scheduler.controller

import com.scheduler.dto.PutGroupRequest
import com.scheduler.dto.PutGroupResponse
import com.scheduler.dto.PutMeResponse
import com.scheduler.security.CustomUserDetails
import com.scheduler.model.Group
import com.scheduler.service.ForbiddenUserException
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
    @PutMapping("/api/v1/groups/{groupId}")
    fun replaceGroup(
        @PathVariable groupId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestBody groupRequest: PutGroupRequest,
    ): ResponseEntity<Void> {
       
       return try {
           putRouteService.replaceGroup(groupId, principal.getId(), groupRequest.groupName, groupRequest.times)
           ResponseEntity.noContent().build()
       }
       catch (e: ImproperTimesFormatException){
          throw ResponseStatusException(
                HttpStatus.EXPECTATION_FAILED,
                "Improper Format of Times"
            )
       }
       catch (e: TimeSlotNotFoundException){
          throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Time Slot Not Found"
            )
       }
       catch (e: GroupNotFoundException){
          throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Group Doesn't Exist"
            )
       }
       catch (e: ForbiddenUserException){
          throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Group created by other not allowed to edit"
            )
       }
    }
    

    //replace all availabilities for a user in a group
    @PutMapping("/api/v1/groups/{groupId}/availability/me")
    fun replaceAvailability(
        @PathVariable groupId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestBody times: String,
    ): ResponseEntity<Void> {
        //Get user id through getOwnProfile to get the userID
        val userId = principal.getId()
       
       return try {
           putRouteService.replaceUserAvailability(groupId, userId, times)
           ResponseEntity.noContent().build()
           
        //    ResponseEntity.status(HttpStatus.CREATED).body(putRouteService.replaceUserAvailability(groupId, userId, times))
       }catch (e: ImproperTimesFormatException){
          throw ResponseStatusException(
                HttpStatus.EXPECTATION_FAILED,
                "Improper Format of Times"
            )
       }
       catch (e: TimeSlotNotFoundException){
          throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Time Slot Not Found"
            )
       }
    }


}