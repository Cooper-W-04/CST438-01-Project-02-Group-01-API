/**
 * Spring Data JPA repository interface for performing database CRUD operations on User entities.
 */
package com.scheduler.repository

import com.scheduler.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersRepository : JpaRepository<User, Long> {
}
