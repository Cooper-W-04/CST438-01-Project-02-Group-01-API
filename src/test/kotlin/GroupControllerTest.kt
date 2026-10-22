package com.scheduler

import com.scheduler.config.SecurityConfig
import com.scheduler.controller.GroupController
import com.scheduler.dto.GroupDto
import com.scheduler.mapper.GroupMapper
import com.scheduler.model.Group
import com.scheduler.model.Member
import com.scheduler.model.User
import com.scheduler.repository.GroupRepository
import com.scheduler.repository.MemberRepository
import com.scheduler.security.CustomUserDetails
import com.scheduler.security.GroupSecurity
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.Optional
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(GroupController::class)
@Import(SecurityConfig::class)
class GroupControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var groupRepository: GroupRepository

    @MockitoBean
    private lateinit var groupMapper: GroupMapper

    @MockitoBean
    private lateinit var memberRepository: MemberRepository

    @MockitoBean(name = "groupSecurity")
    private lateinit var groupSecurity: GroupSecurity

    private val testUser = User(
        id = 10L,
        oauthProvider = null,
        oauthSubject = null,
        displayName = "Test User",
        role = "USER",
        createdAt = LocalDateTime.parse("2026-01-01T12:00:00")
    )

    private val testPrincipal = CustomUserDetails(testUser)

    private fun authenticatedGet(url: String): MockHttpServletRequestBuilder =
        get(url).with(user(testPrincipal))

    @Test
    fun `get group returns group details`() {
        val group = Group(
            id = 1L,
            eventName = "Test Event",
            createdBy = 10L,
            createdAt = OffsetDateTime.parse("2026-01-01T12:00:00Z")
        )

        val dto = GroupDto(
            id = 1L,
            eventName = "Test Event",
            createdBy = 10L,
            createdAt = OffsetDateTime.parse("2026-01-01T12:00:00Z")
        )

        `when`(groupRepository.findById(1L))
            .thenReturn(Optional.of(group))
        `when`(groupMapper.toDto(group)).thenReturn(dto)
        `when`(groupSecurity.isMemberOfGroup(1L, 10L))
            .thenReturn(true)

        mockMvc.perform(
            authenticatedGet("/api/v1/groups/1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.eventName").value("Test Event"))
            .andExpect(jsonPath("$.createdBy").value(10))
    }

    @Test
    fun `get group returns not found for nonexistent group`() {
        `when`(groupSecurity.isMemberOfGroup(5L, 10L))
            .thenReturn(true)
        `when`(groupRepository.findById(5L))
            .thenReturn(Optional.empty())

        mockMvc.perform(authenticatedGet("/api/v1/groups/5"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get group members returns proper list of members`() {
        val member = Member(
            id = 1L,
            groupId = 1L,
            userId = 10L,
            joinedAt = LocalDateTime.parse("2026-01-01T12:00:00")
        )

        `when`(groupSecurity.isMemberOfGroup(1L, 10L))
            .thenReturn(true)
        `when`(groupRepository.existsById(1L)).thenReturn(true)
        `when`(memberRepository.findAllByGroupId(1L))
            .thenReturn(listOf(member))

        mockMvc.perform(
            authenticatedGet("/api/v1/groups/1/members")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].groupId").value(1))
            .andExpect(jsonPath("$[0].userId").value(10))
    }
    //A test to check that if the user isn't in the group, they can't use these functions would be nice,
    //but is not currently implemented.
}
