package com.scheduler.controller

import com.scheduler.model.TimeSlot
import com.scheduler.repository.TimeSlotRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TimeSlotController(
    private val timeSlotRepository: TimeSlotRepository
) {
    //Returns a list of all standard time slots for a week
    @GetMapping("/api/v1/time-slots")
    fun getTimeSlots(): ResponseEntity<List<TimeSlot>> {
        val timeSlots = timeSlotRepository.findAll().toList()
        return ResponseEntity.ok(timeSlots)
    }
}