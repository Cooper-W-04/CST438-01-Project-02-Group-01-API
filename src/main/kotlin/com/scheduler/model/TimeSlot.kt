package com.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("time_slots")
data class TimeSlot(
    @Id
    val id: Long,

    @Column("day_of_week")
    val dayOfWeek: Int,

    @Column("start_hour")
    val startHour: Int,

    @Column("end_hour")
    val endHour: Int
)