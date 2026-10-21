package com.scheduler.service

import com.scheduler.model.Group
import com.scheduler.model.TimeSlot
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import java.sql.Statement

@Service
class PutRouteService(
    private val jdbcTemplate: JdbcTemplate
) {
    fun replaceGroup(groupId: Long, userId: Long, groupName: String, times: String): Boolean {
        //Replace the name with the new given name
        val groups = jdbcTemplate.update(
            """
            UPDATE group
            SET event_name = ?
            WHERE group_id = ?
            """.trimIndent(),
            groupId, groupName

        )
        
        if(groups != 1){
            //Group doesn't exist
            return false
        }
        
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
            val keyHolder = GeneratedKeyHolder()
            if(doesTimeSlotExist((time[1].toString() + time[2]).toInt(),(time[3].toString() + time[4]).toInt())){                
                val timeSlot = TimeSlot(0, time[0].digitToInt(),
                (time[1].toString() + time[2]).toInt(), 
                (time[3].toString() + time[4]).toInt()) 
                
                //Insert the new timeSlot
                jdbcTemplate.update({connection -> connection.prepareStatement(
                    """
                    INSERT INTO time_slots('day_of_the_week','start_hour','end_hour')
                    VALUES(?,?,?)
                    """.trimIndent(),
                    Statement.RETURN_GENERATED_KEYS
                
                ).apply { 
                    setInt(1, timeSlot.dayOfWeek)
                    setInt(2, timeSlot.startHour)
                    setInt(3, timeSlot.endHour)
                    }
                }, keyHolder
                )
            }
            
            
            //Get the id of the timeslot with a Select
            val timeId = getTimeSlot((time[1].toString() + time[2]).toInt(), 
            (time[3].toString() + time[4]).toInt())
            if(timeId == null){
                return false
            }
            
            //Fix this insert
            //Insert the new timeSlot
                jdbcTemplate.update({connection -> connection.prepareStatement(
                    """
                    INSERT INTO availability('user_id','group_id','time_slot_id')
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
    
    fun doesTimeSlotExist(startHour: Int, endHour: Int): Boolean{
        //Put a jbdc retrieve for the time slot
        return jdbcTemplate.queryForObject(
            """
            SELECT EXISTS(
                SELECT 1
                FROM time_slot
                WHERE start_hour = ?
                AND end_hour = ?
            )
            """.trimIndent(), Boolean::class.java,
            startHour,
            endHour,
        ) ?: false   
    }
    
    fun getTimeSlot(startHour: Int, endHour: Int): Long?{
        return jdbcTemplate.queryForObject(
            """
            SELECT id
            FROM time_slot
            WHERE start_hour = ?
            AND end_hour = ?
            """.trimIndent(), Long::class.java,
            startHour, 
            endHour)
    }
}