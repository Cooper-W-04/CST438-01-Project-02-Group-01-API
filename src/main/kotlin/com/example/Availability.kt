package com.example

import org.springframework.data.relational.core.mapping.Table

@Table("AVAILABILITY")
data class Availability(
    val id: Long,
    val group_id: Long,
    val user_id: Long,  
    val time_slot_id: Long,
)