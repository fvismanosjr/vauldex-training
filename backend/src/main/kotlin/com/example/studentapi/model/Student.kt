package com.example.studentapi.model

import com.example.studentapi.entity.Degree
import com.example.studentapi.entity.Student
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import java.time.Instant
import java.time.LocalDate

data class StudentRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email address")
    val email: String,

    @field:NotNull(message = "Birthdate is required")
    @field:Past(message = "Birthdate should be in the past")
    val birthdate: LocalDate?,

    @field:NotBlank(message = "Degree is required")
    val degree: String,
) {
    fun toEntity(degree: Degree): Student {
        return Student(
            name = this.name.trim(),
            email = this.email.lowercase(),
            birthdate = this.birthdate ?: LocalDate.now(),
            degree = degree,
        )
    }
}

data class StudentResponse(
    val id: Long,
    val name: String,
    val email: String,
    val birthdate: LocalDate,
    val degree: String?,
    val userId: Long?,
    val createdAt: Instant?,
    val createdBy: String?,
    val lastModifiedAt: Instant?,
    val lastModifiedBy: String?,
    val deletedAt: Instant?,
    val deletedBy: String?,
)

data class StudentResponseWithoutDegree(
    val id: Long,
    val name: String,
    val email: String,
    val birthdate: LocalDate,
    val createdAt: Instant?,
    val createdBy: String?,
    val lastModifiedAt: Instant?,
    val lastModifiedBy: String?,
    val deletedAt: Instant?,
    val deletedBy: String?,
)