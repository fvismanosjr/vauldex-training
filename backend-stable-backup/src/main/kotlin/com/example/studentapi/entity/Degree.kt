package com.example.studentapi.entity

import com.example.studentapi.model.DegreeResponse
import com.example.studentapi.model.StudentResponseWithoutDegree
import com.example.studentapi.model.DegreeResponseWithListOfStudents
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.Instant

@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(name = "degrees")
class Degree(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name")
    var name: String,

    @Column(name = "abbreviation")
    var abbreviation: String,

    @OneToMany(mappedBy = "degree", fetch = FetchType.LAZY)
    var students: List<Student> = emptyList(),

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "deleted_by", referencedColumnName = "username")
    var deletedBy: User? = null,

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null

) : BaseAuditEntity() {
    fun toDegreeResponse(degree: Degree) : DegreeResponse {
        return DegreeResponse(
            degree.id,
            degree.name,
            degree.abbreviation,
            noOfStudents = degree.students.count(),
            degree.createdAt,
            degree.createdBy?.username,
            degree.lastModifiedAt,
            degree.lastModifiedBy?.username,
            degree.deletedAt,
            degree.deletedBy?.username
        )
    }

    fun toDegreeResponseWithListOfStudents(degree: Degree): DegreeResponseWithListOfStudents {
        return DegreeResponseWithListOfStudents(
            degree.id,
            degree.name,
            degree.abbreviation,
            students = degree.students.map {
                StudentResponseWithoutDegree(
                    it.id,
                    it.name,
                    it.email,
                    it.age,
                    it.createdAt,
                    it.createdBy?.username,
                    it.lastModifiedAt,
                    it.lastModifiedBy?.username,
                    it.deletedAt,
                    it.deletedBy?.username
                )
            },
            degree.createdAt,
            degree.createdBy?.username,
            degree.lastModifiedAt,
            degree.lastModifiedBy?.username,
            degree.deletedAt,
            degree.deletedBy?.username
        )
    }
}