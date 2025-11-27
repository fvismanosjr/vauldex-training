package com.example.studentapi.model

import jakarta.validation.constraints.NotBlank
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.AssertTrue

data class AuthRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,

    @field:NotBlank(message = "Password is required")
    val password: String,

    val confirmPassword: String? = null,
) {
    @AssertTrue(message = "Passwords do not match")
    fun isPasswordMatching(): Boolean {
        return confirmPassword.isNullOrBlank() || password == confirmPassword
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthResponse(
    val token: String? = null,
    val message: String? = null
)