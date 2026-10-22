package com.scheduler.controller

import com.scheduler.dto.PatchGroupRequest
import com.scheduler.security.CustomUserDetails
import com.scheduler.service.PatchRoutesService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class PatchRoutesControllerTest {

    @Mock
    lateinit var patchRoutesService: PatchRoutesService

    @Mock
    lateinit var principal: CustomUserDetails

    private fun controller(): PatchRoutesController {
        return PatchRoutesController(patchRoutesService)
    }

    @Test
    fun `patchGroup returns no content when event name is updated`() {
        `when`(principal.getId()).thenReturn(42L)

        `when`(
            patchRoutesService.patchGroupEventName(
                7L,
                42L,
                "Updated event"
            )
        ).thenReturn(true)

        val response = controller().patchGroup(
            groupId = 7L,
            principal = principal,
            request = PatchGroupRequest(
                eventName = "Updated event"
            )
        )

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(patchRoutesService).patchGroupEventName(
            7L,
            42L,
            "Updated event"
        )
    }

    @Test
    fun `patchGroup returns not found when group cannot be updated`() {
        `when`(principal.getId()).thenReturn(42L)

        `when`(
            patchRoutesService.patchGroupEventName(
                7L,
                42L,
                "Updated event"
            )
        ).thenReturn(false)

        val exception = assertThrows(
            ResponseStatusException::class.java
        ) {
            controller().patchGroup(
                groupId = 7L,
                principal = principal,
                request = PatchGroupRequest(
                    eventName = "Updated event"
                )
            )
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
    }

    @Test
    fun `patchGroup rejects a blank event name`() {
        `when`(principal.getId()).thenReturn(42L)

        `when`(
            patchRoutesService.patchGroupEventName(
                7L,
                42L,
                ""
            )
        ).thenThrow(
            IllegalArgumentException("Event name cannot be blank")
        )

        val exception = assertThrows(
            ResponseStatusException::class.java
        ) {
            controller().patchGroup(
                groupId = 7L,
                principal = principal,
                request = PatchGroupRequest(
                    eventName = ""
                )
            )
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }
}