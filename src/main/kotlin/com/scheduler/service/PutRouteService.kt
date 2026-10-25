package com.scheduler.service

import com.scheduler.dto.PutGroupResponse
import com.scheduler.dto.PutMeResponse
import com.scheduler.model.Group
import com.scheduler.model.TimeSlot
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Statement

class TimeSlotNotFoundException : RuntimeException()
class ImproperTimesFormatException : RuntimeException()
class ForbiddenUserException: RuntimeException()

@Service
class PutRouteService(
    private val jdbcTemplate: JdbcTemplate
    
    
) {
    @Transactional
    fun replaceGroup(groupId: Long, userId: Long, groupName: String, times: String): PutGroupResponse {
        //checks if the group exists
        val groups = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM schedule_groups
            WHERE id = ?
            """.trimIndent(),
            Int::class.java,
            groupId,

        )
        
        if(groups != 1){
            //Group doesn't exist
            throw GroupNotFoundException()
        }
        
        val groupsCheck = jdbcTemplate.update(
            """
            UPDATE schedule_groups
            SET event_name = ?
            WHERE id = ?
            AND created_by = ?
            """.trimIndent(),
            groupName, groupId, userId

        )
        
        if(groupsCheck != 1){
            //Group isn't yours
            throw ForbiddenUserException()
        }
        
        //Then you can replace your own availability or remove yourself but not remove others
        return try {
            PutGroupResponse(groupName, replaceUserAvailability(groupId, userId, times).times)
        }catch (e: ImproperTimesFormatException){
            throw ImproperTimesFormatException()
        }catch (e: TimeSlotNotFoundException){
            throw TimeSlotNotFoundException()
        }
    }
    
    @Transactional
    fun replaceUserAvailability(groupId: Long, userId: Long, times: String): PutMeResponse {
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
            if(time.length != 5){
                throw ImproperTimesFormatException()
            }
            try {
             //check if the day below 0 or above seven
                if(time[0].digitToInt() !in 1..7){
                    throw ImproperTimesFormatException()
                }
            //If this is non numeric catches it and throws back the error
            }catch (e: Exception){
                throw ImproperTimesFormatException()
            }
            //check if either of the hours are below 0 or above 24
            val start = (time[1].toString() + time[2]).toInt()
            val end = (time[3].toString() + time[4]).toInt()
            if(start !in 0..23 || end !in 0..24){
                throw ImproperTimesFormatException()
            }
            if(start >= end){
                throw ImproperTimesFormatException()
            }
            //check if the timeslot is the right size
            if(start != end-1){
                throw ImproperTimesFormatException()
            }   
            //Added time slot verification before deletion
            try {
                val timeId = getTimeSlot((time[1].toString() + time[2]).toInt(), 
            (time[3].toString() + time[4]).toInt(), time[0].digitToInt())
                if(timeId == null){
                    throw TimeSlotNotFoundException()
                }
            }catch (e: EmptyResultDataAccessException){
                throw TimeSlotNotFoundException()
            }

        }
        
        //Once verified delete all the users availabilities from that group
        //Not TimeSlots since many can share a time slot
        val updated = jdbcTemplate.update(
            """
            DELETE FROM availability
            WHERE group_id = ?
              AND user_id = ?
            """.trimIndent(),
            groupId,
            userId
        )
        
        
        for (time in formatTimes){            
            //Already checked timeID above so no need to check again since nothing alters any of the parameters
            val timeId = getTimeSlot((time[1].toString() + time[2]).toInt(), 
            (time[3].toString() + time[4]).toInt(), time[0].digitToInt())
            if(timeId == null){
                throw TimeSlotNotFoundException()
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

        return PutMeResponse(times)
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