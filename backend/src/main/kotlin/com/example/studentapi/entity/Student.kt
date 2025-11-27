package com.example.studentapi.entity

import com.example.studentapi.model.StudentResponse
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
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDate

@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(name = "students")
class Student(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name")
    var name: String,

    @Column(name = "email")
    var email: String,

    @Column(name = "birthdate")
    var birthdate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "degree_id")
    var degree: Degree? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,

) : BaseAuditEntity() {
    fun toStudentResponse() : StudentResponse {
        return StudentResponse(
            this.id,
            this.name,
            this.email,
            this.birthdate,
            this.degree?.abbreviation,
            this.user?.id,
            this.createdAt,
            this.createdBy?.username,
            this.lastModifiedAt,
            this.lastModifiedBy?.username,
            this.deletedAt,
            this.deletedBy?.username
        )
    }
}