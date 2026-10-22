package com.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("group_members")
data class Member (
    @Id
    val id: Long,

    @Column("group_id")
    val groupId: Long,

    @Column("user_id")
    val userId: Long,

    @Column("joined_at")
    val joinedAt: LocalDateTime
)