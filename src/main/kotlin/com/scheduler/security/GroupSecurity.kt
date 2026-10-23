package com.scheduler.security

import com.scheduler.repository.MemberRepository
import org.springframework.stereotype.Component

@Component("groupSecurity")
class GroupSecurity(
    private val memberRepository: MemberRepository
) {
    fun isMemberOfGroup(
        groupId: Long,
        userId: Long
    ): Boolean {
        return memberRepository
            .existsByGroupIdAndUserId(groupId, userId)
    }
}
