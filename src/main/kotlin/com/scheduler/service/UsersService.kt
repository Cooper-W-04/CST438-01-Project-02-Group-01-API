/**
 * Service class containing business logic related to user management and operations.
 */
package com.scheduler.service

import com.scheduler.repository.UsersRepository
import org.springframework.stereotype.Service

@Service
class UsersService(private val usersRepository: UsersRepository) {
    // Add business logic methods here
}
