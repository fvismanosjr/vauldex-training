package com.example.studentapi.controller

import com.example.studentapi.entity.User
import com.example.studentapi.model.AuthRequest
import com.example.studentapi.repository.UserRepository
import com.example.studentapi.service.AuthService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class AuthControllerTest
{
    companion object
    {
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
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var authService: AuthService

    @Autowired
    lateinit var userRepository: UserRepository

    private val objectMapper = jacksonObjectMapper()

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
    fun `Register successfully and return 200 OK`()
    {
        mockMvc
            .perform(
                post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    AuthRequest(
                        "admin",
                        "password"
                    )
                ))
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("User registered successfully"))
    }

    @Test
    fun `Login successfully and return 200 OK`()
    {
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        AuthRequest(
                            "superadmin",
                            "password"
                        )
                    ))
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
    }

    @Test
    fun `Throw exception for username not found and return 400 NOT FOUND`()
    {
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        AuthRequest(
                            "noaccount",
                            "password"
                        )
                    ))
            )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("User not found"))
    }

    @Test
    fun `Throw exception for username already exist and return 500 BAD REQUEST`()
    {
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        AuthRequest(
                            "superadmin",
                            "password"
                        )
                    ))
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("User already exists"))
    }

    @Test
    fun `Throw exception for bad credentials and return 500 BAD CREDENTIALS`()
    {
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        AuthRequest(
                            "superadmin",
                            "wrongpassword"
                        )
                    ))
            )
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.message").value("Invalid credentials"))
    }
}