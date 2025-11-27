package com.example.studentapi.entity

import com.example.studentapi.model.UserResponse
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "username")
    var username: String,

    @JsonIgnore
    @Column(columnDefinition = "TEXT", name = "password")
    var password: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    var role: Role?,

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    var student: Student? = null

) : BaseAuditEntity() {
    fun toUserResponse(): UserResponse
    {
        return UserResponse(
            this.username,
            name = this.student?.name,
            this.createdAt,
            this.createdBy?.username,
            this.lastModifiedAt,
            this.lastModifiedBy?.username,
            this.deletedAt,
            this.deletedBy?.username,
        )
    }
}