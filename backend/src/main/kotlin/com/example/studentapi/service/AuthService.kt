package com.example.studentapi.service

import com.example.studentapi.entity.Student
import com.example.studentapi.entity.User
import com.example.studentapi.exception.UnauthorizedException
import com.example.studentapi.exception.UserAlreadyExistException
import com.example.studentapi.exception.UserNotFoundException
import com.example.studentapi.model.LoginRequest
import com.example.studentapi.model.LoginResponse
import com.example.studentapi.model.RegisterRequest
import com.example.studentapi.model.UserResponse
import com.example.studentapi.repository.RoleRepository
import com.example.studentapi.repository.StudentRepository
import com.example.studentapi.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val studentRepository: StudentRepository,
    private val jwtService: JwtService
) {
    fun getCurrentUser(authentication: Authentication?): UserResponse
    {
        return authentication
            ?.let {
                val user = it.principal as User
                    user.toUserResponse()
            }
            ?: throw UnauthorizedException()
    }

    fun registerUser(request: RegisterRequest) : String {
        userRepository
        .findByUsername(request.email.lowercase())
        ?.let { throw UserAlreadyExistException() }

        val user = userRepository.save(
            User(
                username = request.email.lowercase(),
                password = BCryptPasswordEncoder().encode(request.password),
                role = roleRepository.findByName("ROLE_STUDENT")
            )
        )

        studentRepository
            .findByEmail(request.email.lowercase())
            ?.let { existingStudent ->
                existingStudent.user = user
                studentRepository.save(existingStudent)
            }?: studentRepository.save(
                Student(
                    name = request.name,
                    email = request.email.lowercase(),
                    birthdate = request.birthDate ?: LocalDate.now(),
                    user = user
                )
            )

        return "User registered successfully"
    }

    fun authenticateUser(request: LoginRequest, response: HttpServletResponse) : LoginResponse {
        val user = request
                    .let { userRepository.findByUsername(it.username) ?: throw UserNotFoundException() }
                    .takeIf { BCryptPasswordEncoder().matches(request.password, it.password) }
                    ?: throw BadCredentialsException("Invalid credentials")

        // process cookie
        response.addCookie(jwtService.generateCookie(
            token = jwtService.generateToken(user.username)
        ))

        val name = when (user.role?.name) {
                        "ROLE_STUDENT" -> {
                            studentRepository
                                .findByEmail(user.username)
                                ?.name
                                ?: user.username
                        }
                        else -> {
                            user.username
                        }
                    }

        return LoginResponse(
            id = user.id,
            name = name,
            email = user.username,
            role = user.role?.name ?: "Anonymous",
        )
    }

    fun unAuthenticateUser(response: HttpServletResponse)
    {
        jwtService.clearCookie(response)
    }
}