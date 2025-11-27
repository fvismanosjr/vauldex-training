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
import java.time.Instant

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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "deleted_by", referencedColumnName = "username")
    var deletedBy: User? = null,

    @JsonIgnore
    @Column(name = "deleted_at")
    var deletedAt: Instant? = null

) : BaseAuditEntity() {
    fun toUserResponse(user: User): UserResponse
    {
        return UserResponse(
            user.username,
            user.createdAt,
            user.createdBy?.username,
            user.lastModifiedAt,
            user.lastModifiedBy?.username,
            user.deletedAt,
            user.deletedBy?.username
        )
    }
}