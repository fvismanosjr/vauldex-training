package com.example.studentapi.entity

import com.example.studentapi.model.ChatMessengerResponse
import com.example.studentapi.model.ChatResponse
import com.example.studentapi.model.StudentResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "chats")
class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name")
    var name: String?,

    @ManyToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    var members: List<ChatMember> = emptyList(),

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    var messages: List<ChatMessage> = emptyList()

) : BaseAuditEntity() {
    // for sidebar
    fun toChatResponse(): ChatResponse {
        return ChatResponse(
            this.id,
            this.name
        )
    }

    // for messenger
    fun toChatMessengerResponse(): ChatMessengerResponse {
        return ChatMessengerResponse(
            id = id,
            name = name,
            members = members.map { it.toChatMemberResponse() },
            messages = messages.map { it.toChatMessageResponse() }
        )
    }
}