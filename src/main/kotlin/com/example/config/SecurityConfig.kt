package com.example.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

class SecurityConfig {
    //Makes requesting timeslots public and not require a user
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/time-slots").permitAll()
                it.anyRequest().authenticated()
            }
            .build()
    }
}