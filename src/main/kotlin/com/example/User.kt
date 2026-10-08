package com.example

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class User (
    val id: Long,
    val oauthProvider: String?,
    val oauthSubject: String?,
    val displayName: String,
    val role: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)