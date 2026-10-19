package com.scheduler.security

import com.scheduler.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val user: User
) : UserDetails {

    fun getId(): Long {
        return requireNotNull(user.id)
    }

    override fun getAuthorities(): Collection<out GrantedAuthority> {
        return listOf(
            SimpleGrantedAuthority("USER_${user.role}")
        )
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return requireNotNull(user.id).toString()
    }
}