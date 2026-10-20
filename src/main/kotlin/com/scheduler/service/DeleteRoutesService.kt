package com.scheduler.service

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class DeleteRoutesService(
    private val jdbcTemplate: JdbcTemplate
) {
    fun deleteOwnAccount(userId: Long): Boolean {
        val rowsDeleted = jdbcTemplate.update(
            "DELETE FROM users WHERE id = ?",
            userId
        )

        return rowsDeleted == 1
    }

    fun leaveGroup(groupId: Long, userId: Long): Boolean {
        val rowsDeleted = jdbcTemplate.update(
            """
            DELETE FROM group_members
            WHERE group_id = ?
              AND user_id = ?
            """.trimIndent(),
            groupId,
            userId
        )

        return rowsDeleted == 1
    }

    fun deleteAvailability(availabilityId: Long, userId: Long): Boolean {
        val rowsDeleted = jdbcTemplate.update(
            """
            DELETE FROM availability
            WHERE id = ?
              AND user_id = ?
            """.trimIndent(),
            availabilityId,
            userId
        )

        return rowsDeleted == 1
    }

    fun deleteGroup(groupId: Long): Boolean {
        val rowsDeleted = jdbcTemplate.update(
            "DELETE FROM schedule_groups WHERE id = ?",
            groupId
        )

        return rowsDeleted == 1
    }
}