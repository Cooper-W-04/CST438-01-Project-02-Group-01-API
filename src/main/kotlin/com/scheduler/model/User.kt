package com.scheduler.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "oauth_provider")
    val oauthProvider: String? = null,

    @Column(name = "oauth_subject")
    val oauthSubject: String? = null,

    @Column(name = "display_name", nullable = false)
    val displayName: String,

    @Column(name = "role", nullable = false)
    val role: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)