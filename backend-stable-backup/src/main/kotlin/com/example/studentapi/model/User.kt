package com.example.studentapi.model

import java.time.Instant

data class UserResponse(
    val username: String,
    val createdAt: Instant?,
    val createdBy: String?,
    val lastModifiedAt: Instant?,
    val lastModifiedBy: String?,
    val deletedAt: Instant?,
    val deletedBy: String?,
)