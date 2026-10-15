package com.example.controller

import com.example.`object`.Timeslot
import com.example.repository.TimeslotRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TimeslotController (private val timeslotRepository: TimeslotRepository){
    //Returns a list of all standard timeslots for a week
    @GetMapping("api/v1/time-slots")
    fun getTimeslots() : ResponseEntity<List<Timeslot>> {
        val timeslots = timeslotRepository.findAll().toList()
        return ResponseEntity.ok(timeslots);
    }
}