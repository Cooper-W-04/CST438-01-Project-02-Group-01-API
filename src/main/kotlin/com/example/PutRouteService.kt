package com.example

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class putRouteService(
    private val jdbcTemplate: JdbcTemplate
) {
    fun replaceGroup(groupId: Long, group: Group): Boolean {
        //Replace the name with the name in the passed group
        //Replace the groupMembers
        //Then call replaceGroupAvailability with availabilities
        

        return true
    }

    fun replaceGroupAvailability(groupId: Long, timeSlot: TimeSlot): Boolean {
        //Get all the users from group
        //Then for each user get the corresponeding information
        //Pass it to replaceUserAvailability
        

        return true
    }
    
    fun replaceUserAvailability(groupId: Long, userId: Long, times: String): Boolean {
        val formatTimes = mutableListOf(String)
        //Check if the times string is good and add them to a list
        //this format
        //DayStartEnd
        //Constraints
        //1-7, >=0, <=24
        //20814 71722
        //If anything exceeds the constraints 
        //throw a false and stop here before you add or delete anything
        
        //Once verified delete all the users availabilities from that group
        //Not TimeSlots since many can share a time slot
        jdbcTemplate.update(
            """
            DELETE FROM availability
            WHERE group_id = ?
              AND user_id = ?
            """.trimIndent(),
            groupId,
            userId
        )
        
        //Add all the needed timeSlots if the needed TimeSlot exists already don't make a copy
        //Do that by getting all the time slots and converting them to the string format 
        //if they're .equals don't make a new one
        //Using timeSlot make Availability through the passed along group and userID
        

        return true
    }
}