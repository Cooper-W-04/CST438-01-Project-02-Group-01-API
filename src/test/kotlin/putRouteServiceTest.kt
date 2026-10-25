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
    fun `replaceUserAvailability reports missing time slot`() {
        val service = spy(PutRouteService(jdbc))
        `when`(jdbc.update(anyString(), eq(1L), eq(2L))).thenReturn(1)
        doReturn(null).`when`(service).getTimeSlot(9, 10, 1)

        assertThrows<TimeSlotNotFoundException> {
            service.replaceUserAvailability(1L, 2L, "10910")
        }
    }
    
    @Test
    fun `replaceUserAvailability creates one availability row for each time`() {
        val service = spy(PutRouteService(jdbc))
        doReturn(101L).`when`(service).getTimeSlot(9, 10, 1)
        doReturn(202L).`when`(service).getTimeSlot(10, 11, 1)
        doReturn(1).`when`(jdbc).update(
            any(PreparedStatementCreator::class.java),
            any(KeyHolder::class.java)
        )

        service.replaceUserAvailability(7L, 8L, "10910 11011")

        verify(jdbc).update(anyString(), eq(7L), eq(8L))
        verify(jdbc, times(2)).update(
            any(PreparedStatementCreator::class.java),
            any(KeyHolder::class.java)
        )
    }

    @Test
    fun `replaceUserAvailability does not leave partial availability changes when a time slot is missing`() {
        val service = spy(PutRouteService(jdbc))
        doReturn(101L).`when`(service).getTimeSlot(9, 10, 1)
        doReturn(null).`when`(service).getTimeSlot(10, 11, 1)

        assertThrows<TimeSlotNotFoundException> {
            service.replaceUserAvailability(7L, 8L, "10910 11011")
        }

        // All time slots are checked before the delete or any insert, so an
        // invalid request cannot leave a partially replaced availability set.
        verifyNoInteractions(jdbc)
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
        `when`(
            jdbc.queryForObject(
                anyString(),
                eq(Int::class.java),
                eq(1L)
            )
        ).thenReturn(1)

        `when`(
            jdbc.update(
                anyString(),
                eq("Team"),
                eq(1L),
                eq(2L)
            )
        ).thenReturn(1)

        assertThrows<ImproperTimesFormatException> {
            service.replaceGroup(1L, 2L, "Team", "bad")
        }
    }
    
    
}