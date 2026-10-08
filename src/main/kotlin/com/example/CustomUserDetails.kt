package com.example

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(val user: User) : UserDetails {
    fun getId() : Long {
        return user.id
    }

    //These functions aren't currently necessary so I have left them unimplemented initially
    override fun getAuthorities(): Collection<out GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(user.role))
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername() : String {
        return user.id.toString()
    }
}