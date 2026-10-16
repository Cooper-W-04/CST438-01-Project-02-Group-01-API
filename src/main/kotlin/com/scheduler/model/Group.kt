package com.scheduler.model

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("SCHEDULE_GROUP")
data class Group(
    val id: Long,
    val event_name: String,
    val created_by: User,
    val created_at: LocalDateTime = LocalDateTime.now()
)