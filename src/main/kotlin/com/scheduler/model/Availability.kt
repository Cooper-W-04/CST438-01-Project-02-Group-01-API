package com.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("availability")
data class Availability(
    @Id
    val id: Long,

    @Column("group_id")
    val groupId: Long,

    @Column("user_id")
    val userId: Long,

    @Column("time_slot_id")
    val timeSlotId: Long
)