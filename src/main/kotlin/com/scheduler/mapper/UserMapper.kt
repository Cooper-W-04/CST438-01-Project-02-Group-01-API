package com.scheduler.mapper

import com.scheduler.dto.UserDto
import com.scheduler.model.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun toDto(user: User): UserDto {
        return UserDto(
            id = requireNotNull(user.id),
            displayName = user.displayName,
            role = user.role,
            createdAt = user.createdAt
        )
    }
}