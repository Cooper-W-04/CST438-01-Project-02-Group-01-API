package com.scheduler.service

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.jdbc.core.JdbcTemplate

class PatchRoutesServiceTest {

    private val jdbcTemplate = mock(JdbcTemplate::class.java)
    private val service = PatchRoutesService(jdbcTemplate)

    @Test
    fun `patchGroupEventName updates the event name for the group creator`() {
        `when`(
            jdbcTemplate.update(
                anyString(),
                eq("Updated event"),
                eq(7L),
                eq(42L)
            )
        ).thenReturn(1)

        val result = service.patchGroupEventName(
            groupId = 7L,
            userId = 42L,
            eventName = " Updated event "
        )

        assertTrue(result)

        verify(jdbcTemplate).update(
            anyString(),
            eq("Updated event"),
            eq(7L),
            eq(42L)
        )
    }

    @Test
    fun `patchGroupEventName returns false when user is not the group creator`() {
        `when`(
            jdbcTemplate.update(
                anyString(),
                eq("Updated event"),
                eq(7L),
                eq(42L)
            )
        ).thenReturn(0)

        val result = service.patchGroupEventName(
            groupId = 7L,
            userId = 42L,
            eventName = "Updated event"
        )

        assertFalse(result)
    }

    @Test
    fun `patchGroupEventName rejects a blank event name`() {
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            service.patchGroupEventName(
                groupId = 7L,
                userId = 42L,
                eventName = "   "
            )
        }

        verifyNoInteractions(jdbcTemplate)
    }
}