package com.scheduler.repository

import com.scheduler.model.Member
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : CrudRepository<Member, Long> {
    fun existsByGroupIdAndUserId(
        groupId: Long,
        userId: Long
    ): Boolean

    fun findAllByGroupId(groupId: Long): List<Member>
}