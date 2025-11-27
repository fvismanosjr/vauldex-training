package com.example.studentapi.repository

import com.example.studentapi.entity.ChatMessageType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageTypeRepository: JpaRepository<ChatMessageType, Long> {
}