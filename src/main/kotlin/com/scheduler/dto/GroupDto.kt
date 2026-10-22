package com.scheduler.dto

import java.time.OffsetDateTime

data class GroupDto(
    val id: Long,
    val eventName: String,
    val createdBy: Long,
    val createdAt: OffsetDateTime
)