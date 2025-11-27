package com.example.studentapi.repository

import com.example.studentapi.entity.ChatMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMemberRepository: JpaRepository<ChatMember, Long> {
}