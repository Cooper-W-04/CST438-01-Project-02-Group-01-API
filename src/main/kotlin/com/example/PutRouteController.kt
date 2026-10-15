package com.example

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PathVariable
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
        @RequestBody group: Group,
    ){
       val checker = putRouteService.updateGroup(groupId, group)
       
       if(checker){
          
       }else{
          
       }
    }
    

    //replace all availabilities for a user in a group
    @PutMapping("/api/v1/groups/{groupId}/availability/me")
    fun replaceAvailability(
        @PathVariable groupId: Long,
        @RequestBody times: String,
    ){
        //Get user id through getOwnProfile to get the userID
        val userId = getOwnProfile().id
       val checker = putRouteService.replaceUserAvailability(groupId, userId, times)
       
       if(checker){
          
       }else{
          
       }
    }


}