package com.example

import com.scheduler.controller.PutRouteController
import com.scheduler.dto.PutGroupRequest
import com.scheduler.dto.PutGroupResponse
import com.scheduler.dto.PutMeResponse
import com.scheduler.security.CustomUserDetails
import com.scheduler.service.GroupNotFoundException
import com.scheduler.service.ImproperTimesFormatException
import com.scheduler.service.PutRouteService
import com.scheduler.service.TimeSlotNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
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
    fun `replaceGroup returns created response`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(service.replaceGroup(7L, 42L, "Team", "10912"))
            .thenReturn(PutGroupResponse("Team", "10912"))

        val response = controller.replaceGroup(7L, principal, PutGroupRequest("Team", "10912"))

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(PutGroupResponse("Team", "10912"), response.body)
    }

    @Test
    fun `replaceGroup maps service errors to bad request`() {
        `when`(principal.getId()).thenReturn(42L)
        listOf(
            ImproperTimesFormatException() to "Improper Format of Times",
            TimeSlotNotFoundException() to "Time Slot Not Found",
            GroupNotFoundException() to "Group Doesn't Exist"
        ).forEach { (error, reason) ->
            reset(service)
            `when`(service.replaceGroup(7L, 42L, "Team", "10912")).thenThrow(error)
            val exception = assertThrows<ResponseStatusException> {
                controller.replaceGroup(7L, principal, PutGroupRequest("Team", "10912"))
            }
            assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
            assertEquals(reason, exception.reason)
        }
    }

    @Test
    fun `replaceAvailability returns created response`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(service.replaceUserAvailability(7L, 42L, "10912 21318"))
            .thenReturn(PutMeResponse("10912 21318"))

        val response = controller.replaceAvailability(7L, principal, "10912 21318")

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(PutMeResponse("10912 21318"), response.body)
    }

    @Test
    fun `replaceAvailability maps service errors to bad request`() {
        `when`(principal.getId()).thenReturn(42L)
        listOf(
            ImproperTimesFormatException() to "Improper Format of Times",
            TimeSlotNotFoundException() to "Time Slot Not Found"
        ).forEach { (error, reason) ->
            reset(service)
            `when`(service.replaceUserAvailability(7L, 42L, "10912")).thenThrow(error)
            val exception = assertThrows<ResponseStatusException> {
                controller.replaceAvailability(7L, principal, "10912")
            }
            assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
            assertEquals(reason, exception.reason)
        }
    }
}