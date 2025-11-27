package com.example.studentapi.config

import com.example.studentapi.entity.User
import com.example.studentapi.repository.UserRepository
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.Optional

@Component("auditorProvider")
class AuditorAware(
    private val userRepository: UserRepository
) : AuditorAware<User> {

    override fun getCurrentAuditor(): Optional<User> {
        return Optional.ofNullable((SecurityContextHolder.getContext().authentication?.principal as? User))
    }
}
