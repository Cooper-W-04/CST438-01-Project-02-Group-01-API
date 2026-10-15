package com.example

import com.example.User
import com.example.mapper.UserMapper
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class UserDtoTest {

    @Test
    fun `toDto maps user correctly`() {
        val userMapper = UserMapper()
        val user = User(
            id = 1L,
            oauthProvider = null,
            oauthSubject = null,
            displayName = "Hi",
            role = "ROLE_USER",
            createdAt = LocalDateTime.now()
        )

        val dto = userMapper.toDto(user)

        assertEquals(1L, dto.id)
        assertEquals("Hi", dto.displayName)
        assertEquals("ROLE_USER", dto.role)
    }
}