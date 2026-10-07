package com.example

import java.time.LocalDateTime

data class User (
    val id: Int?,
    val oauthProvider: String?,
    val oauthSubject: String?,
    val displayName: String?,
    val role: String?,
    val createdAt: LocalDateTime?
)