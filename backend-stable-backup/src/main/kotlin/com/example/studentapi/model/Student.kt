package com.example.studentapi.model

import com.example.studentapi.entity.Degree
import com.example.studentapi.entity.Student
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Min
import java.time.Instant

data class StudentRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email address")
    val email: String,

    @field:Min(value = 1, message = "Age must be at least 1")
    val age: Short,

    @field:NotBlank(message = "Degree is required")
    val degree: String,
) {
    fun toEntity(degree: Degree): Student {
        return Student(
            name = this.name.trim(),
            email = this.email.lowercase(),
            age = this.age,
            degree = degree,
        )
    }
}

data class StudentResponse(
    val id: Long,
    val name: String,
    val email: String,
    val age: Short,
    val degree: String?,
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
    val age: Short,
    val createdAt: Instant?,
    val createdBy: String?,
    val lastModifiedAt: Instant?,
    val lastModifiedBy: String?,
    val deletedAt: Instant?,
    val deletedBy: String?,
)