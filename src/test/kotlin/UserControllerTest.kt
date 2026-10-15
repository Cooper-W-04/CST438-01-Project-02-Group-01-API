package com.example

import com.example.controller.UserController
import com.example.dto.UserDto
import com.example.mapper.UserMapper
import com.example.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
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
import org.springframework.context.annotation.Import
import java.time.LocalDateTime
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import java.util.Optional

@WebMvcTest(UserController::class)
@Import(UserMapper::class)
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userRepository: UserRepository

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

        `when`(userRepository.findById(1L))
            .thenReturn(Optional.of(mockUser))

        mockMvc.perform(
            get("/api/v1/users/me")
                .with(user(testPrincipal))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.displayName").value("Hi"))
            .andExpect(jsonPath("\$.role").value("ROLE_USER"))
    }

    @Test
    fun `admins can use getAllUsers and the function works properly`(){
        val createdAt = LocalDateTime.now()
        val mockUser1 = User(
            id = 1L,
            oauthProvider = null,
            oauthSubject = null,
            displayName = "Hi",
            role = "ROLE_USER",
            createdAt = createdAt
        )
        val mockUser2 = User(
            id = 2L,
            oauthProvider = null,
            oauthSubject = null,
            displayName = "Hihi",
            role = "ROLE_USER",
            createdAt = createdAt
        )
        val mockAdmin = User(
            id = 3L,
            oauthProvider = null,
            oauthSubject = null,
            displayName = "Admin",
            role = "ROLE_ADMIN",
            createdAt = createdAt
        )

        val testPrincipal = CustomUserDetails(mockAdmin)

        `when`(userRepository.findAll())
            .thenReturn(listOf(mockUser1, mockUser2, mockAdmin))
        mockMvc.perform(
            get("/api/v1/users")
                .with(user(testPrincipal))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(print())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[2].id").value(3L))
            .andExpect(jsonPath("$[2].role").value("ROLE_ADMIN"))
    }

    @Test
    fun `Admins can view a user by Id` (){
        val createdAt = LocalDateTime.now()
        val mockUser = User(
            id = 1L,
            oauthProvider = null,
            oauthSubject = null,
            displayName = "Hi",
            role = "ROLE_USER",
            createdAt = createdAt
        )
        val mockAdmin = User(
            id = 2L,
            oauthProvider = null,
            oauthSubject = null,
            displayName = "Admin",
            role = "ROLE_ADMIN",
            createdAt = createdAt
        )
        val testPrincipal = CustomUserDetails(mockAdmin)

        val userMapper = UserMapper()

        `when`(userRepository.findById(1L))
            .thenReturn(Optional.of(mockUser))

        mockMvc.perform(
            get("/api/v1/users/1")
                .with(user(testPrincipal))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.displayName").value("Hi"))
    }
}