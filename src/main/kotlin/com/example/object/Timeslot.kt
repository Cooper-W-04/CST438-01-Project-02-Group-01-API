package com.example.`object`

import org.springframework.data.relational.core.mapping.Table

@Table
data class Timeslot (
    val id: Long,
    val dayOfWeek: Int,
    val startHour: Int,
    val endHour: Int
)