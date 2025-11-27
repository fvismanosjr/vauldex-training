package com.example.studentapi.model

import com.example.studentapi.entity.Chat

data class IncomingMessage(
    val user: Long,
    val message: ChatMessageRequest,
    val status: Long,
)

data class OutgoingMessage(
    val id: Long,
    val body: String,
    val user: String?,
    val messageType: String,
    val messageStatus: String,
    val createdAt: String?
)

data class ChatMessageRequest(
    val content: String,
    val type: Long,
)

data class ChatRequest(
    val id: Long,
    val name:  String,
    val members: List<Long> = emptyList(),
    val message: ChatMessageRequest,
) {
    fun toEntity(): Chat {
        return Chat(
            name = this.name,
        )
    }
}

data class ChatResponse(
    val id: Long,
    val name: String?,
)

data class ChatMessengerResponse(
    val id: Long,
    val name: String?,
    val members: List<ChatMemberResponse>,
    val messages: List<ChatMessageResponse>
)

data class ChatMemberResponse(
    val id: Long,
    val user: UserResponse,
    val joinedAt: String,
    val leftAt: String?
)

data class ChatMessageResponse(
    val id: Long,
    val body: String,
    val user: String?,
    val messageType: String,
    val messageStatus: String,
    val createdAt: String
)