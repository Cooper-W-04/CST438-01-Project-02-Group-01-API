package com.example.mapper

import com.example.User
import com.example.dto.UserDto
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun toDto(user: User) : UserDto {
        return UserDto(user.id, user.displayName, user.role, user.createdAt)
    }
}