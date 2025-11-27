package com.example.studentapi.service

import com.example.studentapi.entity.User
import com.example.studentapi.exception.UserAlreadyExistException
import com.example.studentapi.exception.UserNotFoundException
import com.example.studentapi.model.AuthRequest
import com.example.studentapi.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class AuthServiceTest {
    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:15")
            .apply {
                withDatabaseName("studentdb_test")
                withUsername("postgres")
                withPassword("postgres")
            }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }
    }

    @Autowired
    lateinit var authService: AuthService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    val response = MockHttpServletResponse()

    @BeforeEach
    @Transactional
    fun setup()
    {
        userRepository.deleteAll()

        // save 1 record
        userRepository.save(
            User(
                username = "superadmin",
                password = BCryptPasswordEncoder().encode("password")
            )
        )
    }

    @Test
    fun `should successfully register`()
    {
        // when
        val register = authService.registerUser(
            AuthRequest(
                "testuser",
                "testuserpassword"
            )
        )

        // then
        assert(register.message!!.contains("User registered successfully"))
    }

    @Test
    fun `should successfully log in`()
    {
        // when
        val authenticated = authService.authenticateUser(
            AuthRequest(
                "superadmin",
                "password"
            ), response
        )

        // then
        assertNotNull(authenticated.token)
    }

    @Test
    fun `should return an exception if username already exists`()
    {
        // when then
        val exception = assertThrows<UserAlreadyExistException> {
            authService.registerUser(
                AuthRequest(
                    "superadmin",
                    "password"
                )
            )
        }

        // then
        assert(exception.message!!.contains("User already exists"))
    }

    @Test
    fun `should return an exception if user is not found`()
    {
        // when then
        val exception = assertThrows<UserNotFoundException> {
            authService.authenticateUser(
                AuthRequest(
                    "testusernotfound",
                    "password"
                ), response
            )
        }

        // then
        assert(exception.message!!.contains("User not found"))
    }

    @Test
    fun `should return an exception if credentials doesn't match`()
    {
        // when then
        val exception = assertThrows<BadCredentialsException> {
            authService.authenticateUser(
                AuthRequest(
                    "superadmin",
                    "testuserpasswordnotmatch"
                ), response
            )
        }

        // then
        assert(exception.message!!.contains("Invalid credentials"))
    }
}