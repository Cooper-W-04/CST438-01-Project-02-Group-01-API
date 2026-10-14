package com.example.repository

import com.example.`object`.Timeslot
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TimeslotRepository : CrudRepository<Timeslot, Long>{
    //Automatically finds timeslot by slot id
}