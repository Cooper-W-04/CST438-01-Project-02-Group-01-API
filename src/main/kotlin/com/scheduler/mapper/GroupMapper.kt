package com.scheduler.mapper

import com.scheduler.dto.GroupDto
import com.scheduler.model.Group
import org.springframework.stereotype.Component

@Component
class GroupMapper {
    fun toDto(group: Group) : GroupDto {
        return GroupDto(
            id = requireNotNull(group.id),
            eventName = group.eventName,
            createdBy = group.createdBy,
            createdAt = group.createdAt
        )
    }
}