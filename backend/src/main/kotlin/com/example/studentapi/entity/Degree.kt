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
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction

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

    @Column(name = "description")
    var description: String,

    @OneToMany(mappedBy = "degree", fetch = FetchType.LAZY)
    var students: List<Student> = emptyList()

) : BaseAuditEntity() {
    fun toDegreeResponse() : DegreeResponse {
        return DegreeResponse(
            this.id,
            this.name,
            this.abbreviation,
            noOfStudents = this.students.count(),
            this.createdAt,
            this.createdBy?.username,
            this.lastModifiedAt,
            this.lastModifiedBy?.username,
            this.deletedAt,
            this.deletedBy?.username
        )
    }

    fun toDegreeResponseWithListOfStudents(): DegreeResponseWithListOfStudents {
        return DegreeResponseWithListOfStudents(
            this.id,
            this.name,
            this.abbreviation,
            students = this.students.map {
                StudentResponseWithoutDegree(
                    it.id,
                    it.name,
                    it.email,
                    it.birthdate,
                    it.createdAt,
                    it.createdBy?.username,
                    it.lastModifiedAt,
                    it.lastModifiedBy?.username,
                    it.deletedAt,
                    it.deletedBy?.username
                )
            },
            this.createdAt,
            this.createdBy?.username,
            this.lastModifiedAt,
            this.lastModifiedBy?.username,
            this.deletedAt,
            this.deletedBy?.username
        )
    }
}