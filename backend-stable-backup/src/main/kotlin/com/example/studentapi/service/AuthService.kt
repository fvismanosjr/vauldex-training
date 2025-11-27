package com.example.studentapi.service

import com.example.studentapi.entity.Student
import com.example.studentapi.entity.User
import com.example.studentapi.exception.UnauthorizedException
import com.example.studentapi.exception.UserAlreadyExistException
import com.example.studentapi.exception.UserNotFoundException
import com.example.studentapi.model.AuthRequest
import com.example.studentapi.model.AuthResponse
import com.example.studentapi.model.UserResponse
import com.example.studentapi.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {
    fun getCurrentUser(authentication: Authentication?): UserResponse
    {
        return authentication
            ?.let {
                val user = it.principal as User
                    user.toUserResponse(user)
            }
            ?: throw UnauthorizedException()
    }

    fun registerUser(request: AuthRequest) : AuthResponse {
        return userRepository
            .findByUsername(request.username)
            ?.let { throw UserAlreadyExistException() }
            ?: User(
                username = request.username,
                password = BCryptPasswordEncoder().encode(request.password)
            )
            .also { userRepository.save(it) }
            .let { AuthResponse(message = "User registered successfully") }
    }

    fun authenticateUser(request: AuthRequest, response: HttpServletResponse) : AuthResponse {
        val user = request.username
            .let {
                userRepository
                    .findByUsername(it)
                    ?: throw UserNotFoundException()
            }
            .takeIf {
                BCryptPasswordEncoder().matches(request.password, it.password)
            }
            ?: throw BadCredentialsException("Invalid credentials")

        val token = jwtService.generateToken(user.username)
            response.addCookie(jwtService.generateCookie(token))

        return AuthResponse(token = token)
    }

    fun unAuthenticateUser(response: HttpServletResponse)
    {
        jwtService.clearCookie(response)
    }
}