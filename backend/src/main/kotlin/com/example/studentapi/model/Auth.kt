package com.example.studentapi.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import java.time.LocalDate

data class RegisterRequest(
    @field:NotBlank(message = "Full name is required")
    val name: String,

    @field:NotNull(message = "Birthdate is required")
    @field:Past(message = "Birthdate should be in the past")
    val birthDate: LocalDate?, // YYYY-MM-DD

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email address")
    val email: String, // username

    @field:NotBlank(message = "Password is required")
    val password: String,
    val confirmPassword: String? = null,
) {
    @AssertTrue(message = "Passwords do not match")
    fun isPasswordMatch(): Boolean
    {
        return confirmPassword.isNullOrBlank() || password == confirmPassword
    }
}

data class LoginRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,

    @field:NotBlank(message = "Password is required")
    val password: String,
)

data class LoginResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
)