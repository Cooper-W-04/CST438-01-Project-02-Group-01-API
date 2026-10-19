package com.scheduler.repository

import com.scheduler.model.TimeSlot
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TimeSlotRepository : CrudRepository<TimeSlot, Long> {
    //Automatically finds time slots by slot ID
}