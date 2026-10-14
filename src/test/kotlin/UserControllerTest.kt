package com.example

import com.example.controller.UserController
import com.example.dto.UserDto
import com.example.mapper.UserMapper
import com.example.repository.UserRepository
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import java.util.Optional

@WebMvcTest(UserController::class)
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userRepository: UserRepository
    @MockitoBean
    private lateinit var userMapper: UserMapper

    @Test
    fun `getOwnProfile gets appropriately accurate information`(){
        val createdAt = LocalDateTime.now()
        val mockUser = User(
            id = 1L,
            oauthProvider = null,
            oauthSubject = null,
            displayName = "Hi",
            role = "ROLE_USER",
            createdAt = createdAt
        )

        val testPrincipal = CustomUserDetails(mockUser)

        val mockDto = UserDto(
            mockUser.id,
            mockUser.displayName,
            mockUser.role,
            mockUser.createdAt
        )

        `when`(userRepository.findById(1L))
            .thenReturn(Optional.of(mockUser))

        `when`(userMapper.toDto(mockUser))
            .thenReturn(mockDto)

        mockMvc.perform(
            get("/api/v1/users/me")
                .with(user(testPrincipal))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.displayName").value("Hi"))
            .andExpect(jsonPath("\$.role").value("ROLE_USER"))
    }
}