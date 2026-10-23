package com.example

import com.scheduler.dto.PutGroupResponse
import com.scheduler.service.GroupNotFoundException
import com.scheduler.service.ImproperTimesFormatException
import com.scheduler.service.PutRouteService
import com.scheduler.service.TimeSlotNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.KeyHolder

class PutRouteServiceTest {
    private val jdbc = mock(JdbcTemplate::class.java)
    private val service = PutRouteService(jdbc)

    @ParameterizedTest
    @ValueSource(strings = ["", "1234567", "12345", "90809", "10913"])
    fun `replaceUserAvailability rejects invalid times`(times: String) {
        assertThrows<ImproperTimesFormatException> {
            service.replaceUserAvailability(1L, 2L, times)
        }
        verifyNoInteractions(jdbc)
    }

    @Test
    fun `replaceUserAvailability returns times`() {
        `when`(jdbc.update(anyString(), eq(1L), eq(2L))).thenReturn(1)
        `when`(jdbc.queryForObject(anyString(), eq(Long::class.java), eq(9), eq(10), eq(1)))
            .thenReturn(3L)
        `when`(jdbc.update(any<PreparedStatementCreator>(), any<KeyHolder>())).thenReturn(1)

        assertEquals("10910", service.replaceUserAvailability(1L, 2L, "10910").times)
    }

    @Test
    fun `replaceUserAvailability reports missing time slot`() {
        val service = spy(PutRouteService(jdbc))
        `when`(jdbc.update(anyString(), eq(1L), eq(2L))).thenReturn(1)
        doReturn(null).`when`(service).getTimeSlot(9, 10, 1)

        assertThrows<TimeSlotNotFoundException> {
            service.replaceUserAvailability(1L, 2L, "10910")
        }
    }

    @Test
    fun `replaceGroup returns updated group`() {
        `when`(jdbc.update(anyString(), eq("Team"), eq(1L))).thenReturn(1)
        `when`(jdbc.update(anyString(), eq(1L), eq(2L))).thenReturn(1)
        `when`(jdbc.queryForObject(anyString(), eq(Long::class.java), eq(9), eq(10), eq(1)))
            .thenReturn(3L)
        `when`(jdbc.update(any<PreparedStatementCreator>(), any<KeyHolder>())).thenReturn(1)

        assertEquals(PutGroupResponse("Team", "10910"), service.replaceGroup(1L, 2L, "Team", "10910"))
    }

    @Test
    fun `replaceGroup reports missing group`() {
        `when`(jdbc.update(anyString(), eq("Team"), eq(1L))).thenReturn(0)

        assertThrows<GroupNotFoundException> {
            service.replaceGroup(1L, 2L, "Team", "10910")
        }
    }

    @Test
    fun `replaceGroup converts availability errors to invalid times`() {
        `when`(jdbc.update(anyString(), eq("Team"), eq(1L))).thenReturn(1)

        assertThrows<ImproperTimesFormatException> {
            service.replaceGroup(1L, 2L, "Team", "bad")
        }
    }
}