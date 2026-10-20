package com.scheduler.service

import com.scheduler.model.Group
import com.scheduler.model.TimeSlot
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class PutRouteService(
    private val jdbcTemplate: JdbcTemplate
) {
    fun replaceGroup(groupId: Long, userId: Long, groupName: String, times: String): Boolean {
        //Replace the name with the new given name
        jdbcTemplate.update(
            """
            FROM group
            WHERE group_id = ?
            SET event_name = ?
            """.trimIndent(),
            groupId, groupName

        )
        
        //Then you can replace your own availability or remove yourself but not remove others
        try {
            replaceUserAvailability(groupId, userId,times)
        }catch (e: Exception){
            return false
        }
        

        return true
    }
    
    fun replaceUserAvailability(groupId: Long, userId: Long, times: String): Boolean {
        val formatTimes = times.split(" ")
        //Check if the times string is good and add them to a list
        //this format
        //DayStartEnd
        //Constraints
        //1-7, >=0, <=24
        //20814 71722
        //If anything exceeds the constraints 
        //throw a false and stop here before you add or delete anything
        for (time in formatTimes){
            try {
                if(time.length != 5){
                    return false
                }
                //check if the day below 0 or above seven
                if(time[0].digitToInt() !in 0..7){
                    return false
                }
                //check if either of the hours are below 0 or above 24
                val start = (time[1].toString() + time[2]).toInt()
                val end = (time[3].toString() + time[4]).toInt()
                if(start !in 0..24 || end !in 0..24){
                    return false
                }
            }
            catch (e: Exception){
                return false
            }
        }
        
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
        for (time in formatTimes){
            //Check if it already exists when you have a get going for this
            
            val timeSlot = TimeSlot(id = 0, time[0].digitToInt(),
                (time[1].toString() + time[2]).toInt(), 
                (time[3].toString() + time[4]).toInt())
            //In the future replace this with whatever thing we have for Post TimeSlot
            jdbcTemplate.update(
            """
            INSERT INTO TIME_SLOT('day_of_the_week','start_hour','end_hour')
            VALUES(?,?,?)
            """.trimIndent(),
            timeSlot.dayOfWeek,
            timeSlot.startHour,
            timeSlot.endHour
            )
            
            //Get the proper time_slot_id
            jdbcTemplate.update(
            """
            INSERT INTO AVAILABILITY('user_id','group_id','time_slot_id')
            VALUES(?,?,?)
            """.trimIndent(),
            userId,
            groupId,
            timeSlot.id
            )
        }

        return true
    }
}