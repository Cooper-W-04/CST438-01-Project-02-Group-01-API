package com.scheduler

import com.scheduler.config.SecurityConfig
import com.scheduler.controller.PostRoutesController
import com.scheduler.dto.GroupMembershipResponse
import com.scheduler.dto.GroupResponse
import com.scheduler.model.User
import com.scheduler.security.CustomUserDetails
import com.scheduler.service.PostRoutesService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.OffsetDateTime

@WebMvcTest(PostRoutesController::class)
@Import(SecurityConfig::class)
class PostRoutesControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var postRoutesService: PostRoutesService

    @Test
    fun `create group route returns the created group`() {
        val createdGroup = GroupResponse(
            id = 100L,
            eventName = "Test Event",
            createdBy = 1L,
            createdAt = OffsetDateTime.parse("2026-10-20T10:00:00Z")
        )

        `when`(
            postRoutesService.createGroup(
                eventName = "Test Event",
                userId = 1L
            )
        ).thenReturn(createdGroup)

        mockMvc.perform(
            post("/api/v1/groups")
                .with(authentication(authenticationForUser(1L)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"eventName":"Test Event"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.eventName").value("Test Event"))
            .andExpect(jsonPath("$.createdBy").value(1))
    }

    @Test
    fun `join group route returns the created membership`() {
        val membership = GroupMembershipResponse(
            id = 200L,
            groupId = 100L,
            userId = 1L,
            joinedAt = OffsetDateTime.parse("2026-10-20T10:00:00Z")
        )

        `when`(
            postRoutesService.joinGroup(
                groupId = 100L,
                userId = 1L
            )
        ).thenReturn(membership)

        mockMvc.perform(
            post("/api/v1/groups/100/members")
                .with(authentication(authenticationForUser(1L)))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(200))
            .andExpect(jsonPath("$.groupId").value(100))
            .andExpect(jsonPath("$.userId").value(1))
    }

    private fun authenticationForUser(
        userId: Long
    ): UsernamePasswordAuthenticationToken {
        val userDetails = CustomUserDetails(
            User(
                id = userId,
                displayName = "Test User",
                role = "USER"
            )
        )

        return UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.authorities
        )
    }
}