package com.example.studentapi.entity

import com.example.studentapi.model.ChatMessageResponse
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "chat_messages")
class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    var chat: Chat,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    @Column(name = "body")
    var body: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_type_id")
    var chatMessageType: ChatMessageType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_status_id")
    var chatMessageStatus: ChatMessageStatus

) : BaseAuditEntity() {
    fun toChatMessageResponse(): ChatMessageResponse {
        return ChatMessageResponse(
            id = this.id,
            body = this.body,
            user = this.user.student?.name ?: this.user.username,
            messageType = this.chatMessageType.name,
            messageStatus = this.chatMessageStatus.name,
            createdAt = this.createdAt.toString()
        )
    }
}