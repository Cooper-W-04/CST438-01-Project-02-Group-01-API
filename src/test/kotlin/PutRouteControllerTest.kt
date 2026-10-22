package com.example

import com.scheduler.controller.PutRouteController
import com.scheduler.dto.PutGroupRequest
import com.scheduler.security.CustomUserDetails
import com.scheduler.service.PutRouteService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class PutRouteControllerTest {

    @Mock
    lateinit var putRouteService: PutRouteService

    @Mock
    lateinit var principal: CustomUserDetails

    private fun controller() = PutRouteController(putRouteService)

    @Test
    fun `replaceGroup returns 204 when service succeeds`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(putRouteService.replaceGroup(7L, 42L, "Team meeting", "10912"))
            .thenReturn(true)

        val response = controller().replaceGroup(
            groupId = 7L,
            principal = principal,
            groupRequest = PutGroupRequest("Team meeting", "10912")
        )

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(putRouteService).replaceGroup(7L, 42L, "Team meeting", "10912")
    }


    @Test
    fun `replaceAvailability returns 204 when service succeeds`() {
        `when`(principal.getId()).thenReturn(42L)
        `when`(putRouteService.replaceUserAvailability(7L, 42L, "10912 21318"))
            .thenReturn(true)

        val response = controller().replaceAvailability(7L, principal, "10912 21318")

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(putRouteService).replaceUserAvailability(7L, 42L, "10912 21318")
    }
}
