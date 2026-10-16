package com.scheduler.model

import org.springframework.data.relational.core.mapping.Table

@Table("TIME_SLOT")
data class TimeSlot(
    val id: Long,
    val dayOfWeek: Int,
    val startHour: Int,
    val endHour: Int,
)