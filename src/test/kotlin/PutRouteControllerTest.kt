package com.example

import com.scheduler.controller.PutRouteController
import com.scheduler.dto.PutGroupRequest
import com.scheduler.dto.PutGroupResponse
import com.scheduler.dto.PutMeResponse
import com.scheduler.security.CustomUserDetails
import com.scheduler.service.ForbiddenUserException
import com.scheduler.service.GroupNotFoundException
import com.scheduler.service.ImproperTimesFormatException
import com.scheduler.service.PutRouteService
import com.scheduler.service.TimeSlotNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class PutRouteControllerTest {
    @Mock lateinit var service: PutRouteService
    @Mock lateinit var principal: CustomUserDetails

    private val controller get() = PutRouteController(service)

    @Test
    fun `replaceGroup returns no content when group is replaced`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(service.replaceGroup(7L, 42L, "Team", "10912"))
            .thenReturn(PutGroupResponse("Team", "10912"))

        val response = controller.replaceGroup(
            7L,
            principal,
            PutGroupRequest("Team", "10912")
        )

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `replaceGroup returns expectation failed for improper times formatting`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(service.replaceGroup(7L, 42L, "Team", "10912"))
            .thenThrow(ImproperTimesFormatException())

        val exception = assertThrows<ResponseStatusException> {
            controller.replaceGroup(7L, principal, PutGroupRequest("Team", "10912"))
        }

        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.statusCode)
        assertEquals("Improper Format of Times", exception.reason)
    }

    @Test
    fun `replaceGroup returns not found when time slot does not exist`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(service.replaceGroup(7L, 42L, "Team", "10912"))
            .thenThrow(TimeSlotNotFoundException())

        val exception = assertThrows<ResponseStatusException> {
            controller.replaceGroup(7L, principal, PutGroupRequest("Team", "10912"))
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("Time Slot Not Found", exception.reason)
    }

    @Test
    fun `replaceGroup returns not found when group does not exist`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(service.replaceGroup(7L, 42L, "Team", "10912"))
            .thenThrow(GroupNotFoundException())

        val exception = assertThrows<ResponseStatusException> {
            controller.replaceGroup(7L, principal, PutGroupRequest("Team", "10912"))
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("Group Doesn't Exist", exception.reason)
    }

    @Test
    fun `replaceGroup returns forbidden when user is not the group creator`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(service.replaceGroup(7L, 42L, "Team", "10912"))
            .thenThrow(ForbiddenUserException())

        val exception = assertThrows<ResponseStatusException> {
            controller.replaceGroup(7L, principal, PutGroupRequest("Team", "10912"))
        }
        
        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
    }
    @Test
    fun `replaceAvailability returns no content when availability is replaced`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(service.replaceUserAvailability(7L, 42L, "10912 21318"))
            .thenReturn(PutMeResponse("10912 21318"))

        val response = controller.replaceAvailability(7L, principal, "10912 21318")

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `replaceAvailability returns expectation failed for improper times formatting`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(service.replaceUserAvailability(7L, 42L, "10912"))
            .thenThrow(ImproperTimesFormatException())

        val exception = assertThrows<ResponseStatusException> {
            controller.replaceAvailability(7L, principal, "10912")
        }

        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.statusCode)
        assertEquals("Improper Format of Times", exception.reason)
    }

    @Test
    fun `replaceAvailability returns not found when time slot does not exist`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(service.replaceUserAvailability(7L, 42L, "10912"))
            .thenThrow(TimeSlotNotFoundException())

        val exception = assertThrows<ResponseStatusException> {
            controller.replaceAvailability(7L, principal, "10912")
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("Time Slot Not Found", exception.reason)
    }
}

