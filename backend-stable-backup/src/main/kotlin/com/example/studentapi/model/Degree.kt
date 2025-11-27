package com.example.studentapi.model

import com.example.studentapi.entity.Degree
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class DegreeRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Abbreviation is required")
    val abbreviation: String,
) {
    fun toDegree(): Degree
    {
        return Degree(
            name = this.name.trim(),
            abbreviation = this.abbreviation.trim()
        )
    }
}

data class DegreeResponse(
    val id: Long,
    val name: String,
    val abbreviation: String,
    val noOfStudents: Int,
    val createdAt: Instant?,
    val createdBy: String?,
    val lastModifiedAt: Instant?,
    val lastModifiedBy: String?,
    val deletedAt: Instant?,
    val deletedBy: String?,
)

data class DegreeResponseWithListOfStudents(
    val id: Long,
    val name: String,
    val abbreviation: String,
    val students: List<StudentResponseWithoutDegree>,
    val createdAt: Instant?,
    val createdBy: String?,
    val lastModifiedAt: Instant?,
    val lastModifiedBy: String?,
    val deletedAt: Instant?,
    val deletedBy: String?,
)