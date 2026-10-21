package com.scheduler.dto

import java.time.OffsetDateTime

data class GroupMembershipResponse(
    val id: Long,
    val groupId: Long,
    val userId: Long,
    val joinedAt: OffsetDateTime
)