package com.scheduler

import com.scheduler.service.DeleteRoutesService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Testcontainers
class DeleteRoutesServiceIntegrationTest {

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>("postgres:16-alpine")
    }

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var deleteRoutesService: DeleteRoutesService

    @BeforeEach
    fun setUp() {
        val dataSource = DriverManagerDataSource().apply {
            setDriverClassName("org.postgresql.Driver")
            url = postgres.jdbcUrl
            username = postgres.username
            password = postgres.password
        }

        jdbcTemplate = JdbcTemplate(dataSource)
        deleteRoutesService = DeleteRoutesService(jdbcTemplate)

        createTables()
        insertTestData()
    }

    private fun createTables() {
        jdbcTemplate.execute(
            """
            drop table if exists availability, group_members, schedule_groups, time_slots, users cascade
            """.trimIndent()
        )

        jdbcTemplate.execute(
            """
            create table users (
                id bigint primary key,
                oauth_provider text not null,
                oauth_subject text not null,
                display_name text not null,
                role text not null
            )
            """.trimIndent()
        )

        jdbcTemplate.execute(
            """
            create table schedule_groups (
                id bigint primary key,
                event_name text not null,
                created_by bigint not null,
                foreign key (created_by)
                    references users(id)
                    on delete cascade
            )
            """.trimIndent()
        )

        jdbcTemplate.execute(
            """
            create table group_members (
                id bigint primary key,
                group_id bigint not null,
                user_id bigint not null,
                foreign key (group_id)
                    references schedule_groups(id)
                    on delete cascade,
                foreign key (user_id)
                    references users(id)
                    on delete cascade,
                unique (group_id, user_id)
            )
            """.trimIndent()
        )

        jdbcTemplate.execute(
            """
            create table time_slots (
                id bigint primary key,
                day_of_week smallint not null,
                start_hour smallint not null,
                end_hour smallint not null
            )
            """.trimIndent()
        )

        jdbcTemplate.execute(
            """
            create table availability (
                id bigint primary key,
                group_id bigint not null,
                user_id bigint not null,
                time_slot_id bigint not null,
                foreign key (time_slot_id)
                    references time_slots(id)
                    on delete cascade,
                foreign key (group_id, user_id)
                    references group_members(group_id, user_id)
                    on delete cascade
            )
            """.trimIndent()
        )
    }

    private fun insertTestData() {
        jdbcTemplate.update(
            """
            insert into users
                (id, oauth_provider, oauth_subject, display_name, role)
            values
                (1, 'test', 'subject-1', 'Test Admin', 'ADMIN'),
                (2, 'test', 'subject-2', 'Test User Two', 'USER')
            """.trimIndent()
        )

        jdbcTemplate.update(
            """
            insert into schedule_groups
                (id, event_name, created_by)
            values
                (100, 'Test Group', 1)
            """.trimIndent()
        )

        jdbcTemplate.update(
            """
            insert into group_members
                (id, group_id, user_id)
            values
                (200, 100, 1),
                (201, 100, 2)
            """.trimIndent()
        )

        jdbcTemplate.update(
            """
            insert into time_slots
                (id, day_of_week, start_hour, end_hour)
            values
                (300, 1, 9, 10),
                (301, 1, 10, 11)
            """.trimIndent()
        )

        jdbcTemplate.update(
            """
            insert into availability
                (id, group_id, user_id, time_slot_id)
            values
                (400, 100, 1, 300),
                (401, 100, 2, 301)
            """.trimIndent()
        )
    }

    @Test
    fun `deleting own account removes the account and related records`() {
        val deleted = deleteRoutesService.deleteOwnAccount(2)

        assertTrue(deleted)
        assertEquals(0, count("users", "id = 2"))
        assertEquals(0, count("group_members", "user_id = 2"))
        assertEquals(0, count("availability", "user_id = 2"))

        assertEquals(1, count("schedule_groups", "id = 100"))
        assertEquals(1, count("users", "id = 1"))
    }

    @Test
    fun `leaving a group removes only the current user's membership`() {
        val deleted = deleteRoutesService.leaveGroup(100, 2)

        assertTrue(deleted)
        assertEquals(0, count("group_members", "group_id = 100 and user_id = 2"))
        assertEquals(0, count("availability", "group_id = 100 and user_id = 2"))

        assertEquals(1, count("group_members", "group_id = 100 and user_id = 1"))
        assertEquals(1, count("schedule_groups", "id = 100"))
        assertEquals(1, count("users", "id = 2"))
    }

    @Test
    fun `deleting availability only works for the owner`() {
        val deleted = deleteRoutesService.deleteAvailability(401, 2)

        assertTrue(deleted)
        assertEquals(0, count("availability", "id = 401"))

        jdbcTemplate.update(
            """
            insert into availability
                (id, group_id, user_id, time_slot_id)
            values
                (402, 100, 2, 301)
            """.trimIndent()
        )

        val unauthorizedDelete =
            deleteRoutesService.deleteAvailability(402, 1)

        assertFalse(unauthorizedDelete)
        assertEquals(1, count("availability", "id = 402"))
    }

    @Test
    fun `deleting a group removes the group and related records`() {
        val deleted = deleteRoutesService.deleteGroup(100)

        assertTrue(deleted)
        assertEquals(0, count("schedule_groups", "id = 100"))
        assertEquals(0, count("group_members", "group_id = 100"))
        assertEquals(0, count("availability", "group_id = 100"))

        assertEquals(2, count("users", "id > 0"))
        assertEquals(2, count("time_slots", "id > 0"))
    }

    @Test
    fun `deleting a nonexistent group returns false`() {
        val deleted = deleteRoutesService.deleteGroup(999)

        assertFalse(deleted)
    }

    private fun count(table: String, condition: String): Int {
        return jdbcTemplate.queryForObject(
            "select count(*) from $table where $condition",
            Int::class.java
        ) ?: 0
    }
}