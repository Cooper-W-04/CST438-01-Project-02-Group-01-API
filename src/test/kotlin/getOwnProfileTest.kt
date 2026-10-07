package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import java.time.LocalDateTime

class getOwnProfileTest {
    @Test
    fun `getOwnProfile gets appropriately accurate information`(){
        val application = DemoApplication()
        val createdAt = LocalDateTime.now()

        val result = application.getOwnProfile(displayName = "Hi",
            role = "user",
            createdAt = createdAt)

        assertEquals(result.displayName, "Hi")
        assertEquals(result.role, "user")
        assertEquals(result.createdAt, createdAt);
    }
}