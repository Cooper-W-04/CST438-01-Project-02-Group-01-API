/**
 * Service class containing business logic related to user management and operations.
 */
package com.scheduler.service

import com.scheduler.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    // Add business logic methods here
}
