package com.example.dto

import java.time.LocalDateTime

//A UserDto so only relevant information is obtained from the controller; UserController
//does not obtain OAuth information.
data class UserDto (
    val id: Long,
    val displayName: String,
    val role: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)