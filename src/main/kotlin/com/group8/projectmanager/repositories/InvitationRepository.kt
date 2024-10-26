package com.group8.projectmanager.repositories

import com.group8.projectmanager.models.Invitation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.stream.Stream

@Repository
interface InvitationRepository : JpaRepository<Invitation, Long> {
    fun findByReceiverId(id: Long): Stream<Invitation>
    fun findBySenderId(id: Long): Stream<Invitation>
}