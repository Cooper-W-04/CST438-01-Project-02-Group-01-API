package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@SpringBootApplication
@RestController
class DemoApplication {
    @GetMapping("/api/v1/users/me")
    fun getOwnProfile(@RequestParam(value = "display_name") displayName: String?,
                      @RequestParam(value = "role") role: String?,
                      @RequestParam(value = "created_at") createdAt: LocalDateTime?) : User {
        return User(null, null, null, displayName, role, createdAt);
    }
}