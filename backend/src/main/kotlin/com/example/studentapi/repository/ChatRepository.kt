package com.example.studentapi.repository

import com.example.studentapi.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository: JpaRepository<Chat, Long>, JpaSpecificationExecutor<Chat> {
    @Query("SELECT c FROM Chat c JOIN c.members m WHERE m.user.id = :userId")
    fun findChatsByMemberId(@Param("userId") userId: Long): List<Chat>
}