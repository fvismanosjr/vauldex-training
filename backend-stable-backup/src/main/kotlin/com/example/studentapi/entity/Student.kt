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
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.Instant

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

    @Column(name = "age")
    var age: Short,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "degree_id")
    var degree: Degree,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "deleted_by", referencedColumnName = "username")
    var deletedBy: User? = null,

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null

) : BaseAuditEntity() {
    fun toStudentResponse(student: Student) : StudentResponse {
        return StudentResponse(
            student.id,
            student.name,
            student.email,
            student.age,
            student.degree.abbreviation,
            student.createdAt,
            student.createdBy?.username,
            student.lastModifiedAt,
            student.lastModifiedBy?.username,
            student.deletedAt,
            student.deletedBy?.username
        )
    }
}