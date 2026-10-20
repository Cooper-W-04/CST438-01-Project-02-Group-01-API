package com.scheduler.controller

import com.scheduler.security.CustomUserDetails
import com.scheduler.model.Group
import com.scheduler.service.PutRouteService
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
        @RequestBody groupName: String,
        @RequestBody times: String,
    ): ResponseEntity<Void> {

       val checker = putRouteService.replaceGroup(groupId, principal.getId(), groupName, times)
       
       if(checker){
          return ResponseEntity.noContent().build()
       }else{
          throw ResponseStatusException(
                HttpStatus.EXPECTATION_FAILED,
                "Improper Formatting of Time Information"
            )
       }
    }
    

    //replace all availabilities for a user in a group
    @PutMapping("/api/v1/groups/{groupId}/availability/me")
    fun replaceAvailability(
        @PathVariable groupId: Long,
        @RequestBody times: String,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<Void> {
        //Get user id through getOwnProfile to get the userID
        val userId = principal.getId()
       val checker = putRouteService.replaceUserAvailability(groupId, userId, times)
       
       if(checker){
          return ResponseEntity.noContent().build()
       }else{
          throw ResponseStatusException(
                HttpStatus.EXPECTATION_FAILED,
                "Improper Formatting of Time Information"
            )
       }
    }


}