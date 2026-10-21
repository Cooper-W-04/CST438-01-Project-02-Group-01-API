package com.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

@Table("schedule_groups")
data class Group(
    @Id
    val id: Long,

    @Column("event_name")
    val eventName: String,

    @Column("created_by")
    val createdBy: Long,

    @Column("created_at")
    val createdAt: OffsetDateTime
)