/**
 * JPA Entity representing the 'public.users' table in the Supabase PostgreSQL database.
 */
package com.scheduler.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: Long? = null,
    val oauthProvider: String,
    val oauthSubject: String,
    val displayName: String,
    val role: String
)
