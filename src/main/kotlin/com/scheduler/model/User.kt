package com.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class User(
    @Id
    val id: Long? = null,

    @Column("oauth_provider")
    val oauthProvider: String? = null,

    @Column("oauth_subject")
    val oauthSubject: String? = null,

    @Column("display_name")
    val displayName: String,

    @Column("role")
    val role: String,

    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)