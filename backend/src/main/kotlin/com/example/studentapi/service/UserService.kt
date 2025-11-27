package com.example.studentapi.service

import com.example.studentapi.model.UserResponse
import com.example.studentapi.repository.UserRepository
import com.example.studentapi.specification.StringFilterSpecification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun findAll(
        filter: String? = null,
        pageable: Pageable? = Pageable.unpaged()
    ) : Page<UserResponse> {
        return userRepository.findAll(
            StringFilterSpecification(filter).generate(),
            pageable ?: Pageable.unpaged()
        ).map { it.toUserResponse() }
    }
}