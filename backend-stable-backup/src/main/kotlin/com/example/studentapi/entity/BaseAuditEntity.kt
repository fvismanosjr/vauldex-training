package com.example.studentapi.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.OneToOne
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseAuditEntity {

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "created_by", referencedColumnName = "username", updatable = false)
    var createdBy: User? = null

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant? = null

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "last_modified_by", referencedColumnName = "username", updatable = false)
    var lastModifiedBy: User? = null

    @LastModifiedDate
    @Column(name = "last_modified_at")
    var lastModifiedAt: Instant? = null
}