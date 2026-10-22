package com.scheduler.service

import com.scheduler.model.Group
import com.scheduler.model.TimeSlot
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Statement

@Service
class PutRouteService(
    private val jdbcTemplate: JdbcTemplate
) {
    fun replaceGroup(groupId: Long, userId: Long, groupName: String, times: String): Boolean {
        //Replace the name with the new given name
        val groups = jdbcTemplate.update(
            """
            UPDATE schedule_groups
            SET event_name = ?
            WHERE id = ?
            """.trimIndent(),
            groupName, groupId

        )
        
        if(groups != 1){
            //Group doesn't exist
            return false
        }
        
        //Then you can replace your own availability or remove yourself but not remove others
        return try {
            replaceUserAvailability(groupId, userId,times)
        }catch (e: Exception){
            false
        }
        

        return true
    }
    
    @Transactional
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
                if(time[0].digitToInt() !in 1..7){
                    return false
                }
                //check if either of the hours are below 0 or above 24
                val start = (time[1].toString() + time[2]).toInt()
                val end = (time[3].toString() + time[4]).toInt()
                if(start !in 0..24 || end !in 0..24){
                    return false
                }
                if(start >= end){
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
            if(!doesTimeSlotExist((time[1].toString() + time[2]).toInt(),(time[3].toString() + time[4]).toInt(), time[0].digitToInt())){                
                return false
            }
            
            
            //Get the id of the timeslot with a Select
            val timeId = getTimeSlot((time[1].toString() + time[2]).toInt(), 
            (time[3].toString() + time[4]).toInt(), time[0].digitToInt())
            if(timeId == null){
                return false
            }
            
            val keyHolder = GeneratedKeyHolder()
            //Fix this insert
            //Insert the new availability
                jdbcTemplate.update({connection -> connection.prepareStatement(
                    """
                    INSERT INTO availability(user_id,group_id,time_slot_id)
                    VALUES(?,?,?)
                    """.trimIndent(),
                    Statement.RETURN_GENERATED_KEYS
                
                ).apply { 
                    setLong(1, userId)
                    setLong(2, groupId)
                    setLong(3, timeId)
                    }
                }, keyHolder
                )
        }

        return true
    }
    
    fun doesTimeSlotExist(startHour: Int, endHour: Int, day: Int): Boolean{
        //Put a jbdc retrieve for the time slot
        return jdbcTemplate.queryForObject(
            """
            SELECT EXISTS(
                SELECT 1
                FROM time_slots
                WHERE start_hour = ?
                AND end_hour = ?
                AND day_of_the_week = ?
            )
            """.trimIndent(), Boolean::class.java,
            startHour,
            endHour,day
        ) ?: false   
    }
    
    fun getTimeSlot(startHour: Int, endHour: Int, day: Int): Long?{
        return jdbcTemplate.queryForObject(
            """
            SELECT id
            FROM time_slots
            WHERE start_hour = ?
            AND end_hour = ?
            AND day_of_the_week = ?
            """.trimIndent(), Long::class.java,
            startHour, 
            endHour,day)
    }
}