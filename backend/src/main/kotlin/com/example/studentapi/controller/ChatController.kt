package com.example.studentapi.controller

import com.example.studentapi.model.ChatRequest
import com.example.studentapi.model.IncomingMessage
import com.example.studentapi.service.ChatService
import jakarta.validation.Valid
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/chats")
class ChatController(
    private val chatService: ChatService,
) {

    @GetMapping
    @ResponseBody
    fun findAll() = chatService.findAll()

    // sidebar
    @GetMapping("/users/{id}")
    @ResponseBody
    fun getUserChats(
        @PathVariable id: Long,
    ) = chatService.getUserChats(id)

    // messenger
    @GetMapping("/{id}")
    @ResponseBody
    fun find(
        @PathVariable id: Long
    ) = chatService.findById(id)

    // sidebar
    @PostMapping
    @ResponseBody
    fun store(
        @Valid @RequestBody request: ChatRequest
    ) = chatService.save(request)

    // socket
    @MessageMapping("/chat/{channel}")
    fun sendMessage(
        message: IncomingMessage,
        @DestinationVariable channel: String
    ) = chatService.saveMessage(message, channel.toLong())
}
