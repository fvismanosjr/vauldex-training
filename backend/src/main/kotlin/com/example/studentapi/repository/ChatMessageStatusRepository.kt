package com.example.studentapi.repository

import com.example.studentapi.entity.ChatMessageStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageStatusRepository: JpaRepository<ChatMessageStatus, Long> {
}