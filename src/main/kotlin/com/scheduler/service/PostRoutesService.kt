package com.scheduler.service

import com.scheduler.dto.GroupMembershipResponse
import com.scheduler.dto.GroupResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import java.sql.Statement
import java.time.OffsetDateTime

class UserNotFoundException : RuntimeException()

class GroupNotFoundException : RuntimeException()

class AlreadyMemberException : RuntimeException()

@Service
class PostRoutesService(
    private val jdbcTemplate: JdbcTemplate
) {
    fun createGroup(eventName: String, userId: Long): GroupResponse {
        if (eventName.isBlank()) {
            throw IllegalArgumentException("Event name cannot be blank")
        }

        if (!userExists(userId)) {
            throw UserNotFoundException()
        }

        val keyHolder = GeneratedKeyHolder()

        jdbcTemplate.update(
            { connection ->
                connection.prepareStatement(
                    """
                    INSERT INTO schedule_groups (event_name, created_by)
                    VALUES (?, ?)
                    """.trimIndent(),
                    Statement.RETURN_GENERATED_KEYS
                ).apply {
                    setString(1, eventName.trim())
                    setLong(2, userId)
                }
            },
            keyHolder
        )

        val groupId = keyHolder.key?.toLong()
            ?: throw IllegalStateException("Created group did not receive an ID")

        return jdbcTemplate.queryForObject(
            """
            SELECT id, event_name, created_by, created_at
            FROM schedule_groups
            WHERE id = ?
            """.trimIndent(),
            { resultSet, _ ->
                GroupResponse(
                    id = resultSet.getLong("id"),
                    eventName = resultSet.getString("event_name"),
                    createdBy = resultSet.getLong("created_by"),
                    createdAt = resultSet.getObject(
                        "created_at",
                        OffsetDateTime::class.java
                    )
                )
            },
            groupId
        ) ?: throw IllegalStateException("Created group could not be retrieved")
    }

    fun joinGroup(
        groupId: Long,
        userId: Long
    ): GroupMembershipResponse {
        if (!userExists(userId)) {
            throw UserNotFoundException()
        }

        if (!groupExists(groupId)) {
            throw GroupNotFoundException()
        }

        if (membershipExists(groupId, userId)) {
            throw AlreadyMemberException()
        }

        val keyHolder = GeneratedKeyHolder()

        jdbcTemplate.update(
            { connection ->
                connection.prepareStatement(
                    """
                    INSERT INTO group_members (group_id, user_id)
                    VALUES (?, ?)
                    """.trimIndent(),
                    Statement.RETURN_GENERATED_KEYS
                ).apply {
                    setLong(1, groupId)
                    setLong(2, userId)
                }
            },
            keyHolder
        )

        val membershipId = keyHolder.key?.toLong()
            ?: throw IllegalStateException("Created membership did not receive an ID")

        return jdbcTemplate.queryForObject(
            """
            SELECT id, group_id, user_id, joined_at
            FROM group_members
            WHERE id = ?
            """.trimIndent(),
            { resultSet, _ ->
                GroupMembershipResponse(
                    id = resultSet.getLong("id"),
                    groupId = resultSet.getLong("group_id"),
                    userId = resultSet.getLong("user_id"),
                    joinedAt = resultSet.getObject(
                        "joined_at",
                        OffsetDateTime::class.java
                    )
                )
            },
            membershipId
        ) ?: throw IllegalStateException(
            "Created membership could not be retrieved"
        )
    }

    private fun userExists(userId: Long): Boolean {
        return jdbcTemplate.queryForObject(
            """
            SELECT EXISTS(
                SELECT 1
                FROM users
                WHERE id = ?
            )
            """.trimIndent(),
            Boolean::class.java,
            userId
        ) ?: false
    }

    private fun groupExists(groupId: Long): Boolean {
        return jdbcTemplate.queryForObject(
            """
            SELECT EXISTS(
                SELECT 1
                FROM schedule_groups
                WHERE id = ?
            )
            """.trimIndent(),
            Boolean::class.java,
            groupId
        ) ?: false
    }

    private fun membershipExists(
        groupId: Long,
        userId: Long
    ): Boolean {
        return jdbcTemplate.queryForObject(
            """
            SELECT EXISTS(
                SELECT 1
                FROM group_members
                WHERE group_id = ?
                  AND user_id = ?
            )
            """.trimIndent(),
            Boolean::class.java,
            groupId,
            userId
        ) ?: false
    }
}