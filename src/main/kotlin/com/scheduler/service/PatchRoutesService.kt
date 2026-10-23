package com.scheduler.service

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class PatchRoutesService(
    private val jdbcTemplate: JdbcTemplate
) {
    fun patchGroupEventName(
        groupId: Long,
        userId: Long,
        eventName: String
    ): Boolean {
        val cleanedEventName = eventName.trim()

        if (cleanedEventName.isBlank()) {
            throw IllegalArgumentException("Event name cannot be blank")
        }

        val updatedRows = jdbcTemplate.update(
            """
            UPDATE schedule_groups
            SET event_name = ?
            WHERE id = ?
              AND created_by = ?
            """.trimIndent(),
            cleanedEventName,
            groupId,
            userId
        )

        return updatedRows == 1
    }
}