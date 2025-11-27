package com.example.studentapi.repository

import com.example.studentapi.entity.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository: JpaRepository<ChatMessage, Long> {
}