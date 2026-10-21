package com.example

import com.scheduler.service.PutRouteService
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.params.ParameterizedTest
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import kotlin.test.Test
import kotlin.test.assertTrue

class PutRouteServiceTest {

    private val jdbcTemplate = mock(JdbcTemplate::class.java)
    private val service = PutRouteService(jdbcTemplate)

    @ParameterizedTest
    fun `replaceUserAvailability returns false for incorrectly formatted times`(
    ) {
        var times = ""
        var result = 
                service.replaceUserAvailability(
                    groupId = 1L,
                    userId = 2L,
                    times = times
            )
        

        assertFalse(result)
        verifyNoInteractions(jdbcTemplate)
        
        times = "1234567"
        result = 
                service.replaceUserAvailability(
                    groupId = 1L,
                    userId = 2L,
                    times = times
            )
        

        assertFalse(result)
        verifyNoInteractions(jdbcTemplate)
        
        times = "12345"
        result = 
                service.replaceUserAvailability(
                    groupId = 1L,
                    userId = 2L,
                    times = times
            )
        

        assertFalse(result)
        verifyNoInteractions(jdbcTemplate)
        
        times = "90809"
        result = 
                service.replaceUserAvailability(
                    groupId = 1L,
                    userId = 2L,
                    times = times
            )
        

        assertFalse(result)
        verifyNoInteractions(jdbcTemplate)
    }
    
    @Test
    fun `replaceUserAvailability reuses an existing time slot`() {
        `when`(
            jdbcTemplate.queryForObject(
                anyString(),
                eq(Boolean::class.java),
                eq(9),
                eq(17),
                eq(1)
            )
        ).thenReturn(true)

        `when`(
            jdbcTemplate.queryForObject(
                anyString(),
                eq(Long::class.java),
                eq(9),
                eq(17),
                eq(1)
            )
        ).thenReturn(42L)

        val result = service.replaceUserAvailability(
            groupId = 1L,
            userId = 2L,
            times = "10917"
        )

        assertTrue(result)

        verify(jdbcTemplate).update(
            anyString(),
            eq(1L),
            eq(2L)
        )

        verify(jdbcTemplate).queryForObject(
            anyString(),
            eq(Boolean::class.java),
            eq(9),
            eq(17),
            eq(1)
        )

        verify(jdbcTemplate).queryForObject(
            anyString(),
            eq(Long::class.java),
            eq(9),
            eq(17),
            eq(1)
        )
    }

    @Test
    fun `replaceUserAvailability returns false when time slot id cannot be found`() {
        `when`(
            jdbcTemplate.queryForObject(
                anyString(),
                eq(Boolean::class.java),
                eq(9),
                eq(17),
                eq(1)
            )
        ).thenReturn(true)

        `when`(
            jdbcTemplate.queryForObject(
                anyString(),
                eq(Long::class.java),
                eq(9),
                eq(17),
                eq(1)
            )
        ).thenReturn(null)

        val result = service.replaceUserAvailability(
            groupId = 1L,
            userId = 2L,
            times = "10917"
        )

        assertFalse(result)
    }

    @Test
    fun `replaceUserAvailability creates a new time slot when one does not exist`() {
        `when`(
            jdbcTemplate.queryForObject(
                anyString(),
                eq(Boolean::class.java),
                eq(9),
                eq(17),
                eq(1)
            )
        ).thenReturn(false)

        `when`(
            jdbcTemplate.queryForObject(
                anyString(),
                eq(Long::class.java),
                eq(9),
                eq(17),
                eq(1)
            )
        ).thenReturn(42L)

        val result = service.replaceUserAvailability(
            groupId = 1L,
            userId = 2L,
            times = "10917"
        )

        assertTrue(result)

        verify(
            jdbcTemplate,
            times(2)
        ).update(
            any<PreparedStatementCreator>(),
            any<GeneratedKeyHolder>()
        )
    }

    @Test
    fun `replaceGroup returns false when group does not exist`() {
        `when`(
            jdbcTemplate.update(
                anyString(),
                eq("New group name"),
                eq(1L)
            )
        ).thenReturn(0)

        val result = service.replaceGroup(
            groupId = 1L,
            userId = 2L,
            groupName = "New group name",
            times = "10917"
        )

        assertFalse(result)
        verify(jdbcTemplate).update(
            anyString(),
            eq("New group name"),
            eq(1L)
        )
        verifyNoMoreInteractions(jdbcTemplate)
    }

    @Test
    fun `replaceGroup returns false when availability replacement fails`() {
        `when`(
            jdbcTemplate.update(
                anyString(),
                eq("New group name"),
                eq(1L)
            )
        ).thenReturn(1)

        `when`(
            jdbcTemplate.queryForObject(
                anyString(),
                eq(Boolean::class.java),
                any(),
                any(),
                any()
            )
        ).thenThrow(RuntimeException("Database error"))

        val result = service.replaceGroup(
            groupId = 1L,
            userId = 2L,
            groupName = "New group name",
            times = "10917"
        )

        assertFalse(result)
    }
}