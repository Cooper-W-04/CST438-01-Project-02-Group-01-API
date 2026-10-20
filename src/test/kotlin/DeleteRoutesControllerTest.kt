package com.scheduler

import com.scheduler.config.SecurityConfig
import com.scheduler.controller.DeleteRoutesController
import com.scheduler.model.User
import com.scheduler.security.CustomUserDetails
import com.scheduler.service.DeleteRoutesService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(DeleteRoutesController::class)
@Import(SecurityConfig::class)
class DeleteRoutesControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var deleteRoutesService: DeleteRoutesService

    @Test
    fun `admin can delete a group`() {
        val adminUser = User(
            id = 1L,
            displayName = "Test Admin",
            role = "ADMIN"
        )

        val principal = CustomUserDetails(adminUser)

        val authenticationToken = UsernamePasswordAuthenticationToken(
            principal,
            null,
            principal.authorities
        )

        Mockito.`when`(deleteRoutesService.deleteGroup(100L))
            .thenReturn(true)

        mockMvc.perform(
            delete("/api/v1/groups/100")
                .with(authentication(authenticationToken))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `non-admin cannot delete a group`() {
        val regularUser = User(
            id = 2L,
            displayName = "Test User",
            role = "USER"
        )

        val principal = CustomUserDetails(regularUser)

        val authenticationToken = UsernamePasswordAuthenticationToken(
            principal,
            null,
            principal.authorities
        )

        mockMvc.perform(
            delete("/api/v1/groups/100")
                .with(authentication(authenticationToken))
        )
            .andExpect(status().isForbidden)
    }
}