package com.example

import org.springframework.data.relational.core.mapping.Table

@Table("TIME_SLOT")
data class TimeSlot(
    val id: Long,
    val day_of_the_week: Int,
    val start_hour: Int,  
    val end_hour: Int,
)
