package com.example.studentapi.service

import com.example.studentapi.entity.ChatMember
import com.example.studentapi.entity.ChatMessage
import com.example.studentapi.exception.UserNotFoundException
import com.example.studentapi.model.*
import com.example.studentapi.repository.*
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatMemberRepository: ChatMemberRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatMessageTypeRepository: ChatMessageTypeRepository,
    private val chatMessageStatusRepository: ChatMessageStatusRepository,
    private val userRepository: UserRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun findAll() =
        chatRepository.findAll().map { it.toChatResponse() }

    fun getUserChats(id: Long) =
        chatRepository.findChatsByMemberId(id).map { it.toChatResponse() }

    fun findById(id: Long): ChatMessengerResponse =
        chatRepository.findById(id)
            .map { it.toChatMessengerResponse() }
            .orElseThrow { Exception("chat not found") }

    fun save(request: ChatRequest): ChatResponse =
        request.toEntity()
            .run(chatRepository::save)
            .let { chat ->
                // Build members functionally
                val members = request.members.map { userId ->
                    userRepository.findById(userId)
                        .orElseThrow { UserNotFoundException() }
                        .let { user -> ChatMember(chat = chat, user = user, joinedAt = LocalDate.now()) }
                }
                    .also(chatMemberRepository::saveAll) // persist members

                // Fetch type and status functionally
                val type = chatMessageTypeRepository.findById(1)
                    .orElseThrow { Exception("no type") }

                val status = chatMessageStatusRepository.findById(1)
                    .orElseThrow { Exception("no status") }

                // Save initial message
                ChatMessage(
                    chat = chat,
                    user = members.last().user,
                    body = request.message.content,
                    chatMessageType = type,
                    chatMessageStatus = status
                ).also(chatMessageRepository::save)

                ChatResponse(chat.id, chat.name)
            }

    fun saveMessage(message: IncomingMessage, channel: Long) {
        val chat = chatRepository.findById(channel)
            .orElseThrow { Exception("chat not found") }

        val user = userRepository.findById(message.user)
            .orElseThrow { Exception("user not found") }

        val type = chatMessageTypeRepository.findById(message.message.type)
            .orElseThrow { Exception("no type") }

        val status = chatMessageStatusRepository.findById(message.status)
            .orElseThrow { Exception("no status") }

        // Create and persist message functionally
        val chatMessage = ChatMessage(
            chat = chat,
            user = user,
            body = message.message.content,
            chatMessageType = type,
            chatMessageStatus = status
        ).run(chatMessageRepository::save)

        // Build outgoing message
        OutgoingMessage(
            id = chatMessage.id,
            body = message.message.content,
            user = user.student?.name ?: user.username,
            messageType = type.name,
            messageStatus = status.name,
            createdAt = chatMessage.createdAt.toString()
        ).also { outgoing ->
            messagingTemplate.convertAndSend("/topic/channel.$channel", outgoing)
        }
    }
}
