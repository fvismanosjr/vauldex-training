package com.example.studentapi.entity

import com.example.studentapi.model.ChatMemberResponse
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
import java.time.LocalDate

@Entity
@Table(name = "chat_members")
class ChatMember(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    var chat: Chat,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    @Column(name = "joined_at")
    var joinedAt: LocalDate,

    @Column(name = "left_at")
    var leftAt: LocalDate? = null,
) : BaseAuditEntity() {
    fun toChatMemberResponse(): ChatMemberResponse {
        return ChatMemberResponse(
            id = this.id,
            user = this.user.toUserResponse(),
            joinedAt = this.joinedAt.toString(),
            leftAt = this.leftAt.toString(),
        )
    }
}