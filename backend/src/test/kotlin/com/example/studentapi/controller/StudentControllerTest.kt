package com.example.studentapi.controller

import com.example.studentapi.entity.Degree
import com.example.studentapi.entity.Student
import com.example.studentapi.model.AuthRequest
import com.example.studentapi.model.StudentRequest
import com.example.studentapi.repository.DegreeRepository
import com.example.studentapi.repository.StudentRepository
import com.example.studentapi.repository.UserRepository
import com.example.studentapi.service.AuthService
import com.example.studentapi.service.JwtService
import com.example.studentapi.service.StudentService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.properties.Delegates
import jakarta.servlet.http.Cookie

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class StudentControllerTest {
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
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var degreeRepository: DegreeRepository

    @Autowired
    lateinit var authService: AuthService

    @Autowired
    lateinit var jwtService: JwtService

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    val response = MockHttpServletResponse()

    private var cookie: Cookie by Delegates.notNull()
    private var insertedStudentId: Long by Delegates.notNull()
    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    @Transactional
    fun setup()
    {
        degreeRepository.deleteAll()
        studentRepository.deleteAll()
        userRepository.deleteAll()

        // try to register then logged in a user
        authService.registerUser(
            AuthRequest(
                "superadmin",
                "password"
            )
        )

        val authenticatedUser = authService.authenticateUser(
            AuthRequest(
                "superadmin",
                "password"
            ), response
        )

        cookie = jwtService.generateCookie(authenticatedUser.token.toString())

        val degrees = degreeRepository.saveAll(
            listOf(
                Degree(name = "Bachelor of Science in Information Technology", abbreviation = "BSIT"),
                Degree(name = "Bachelor of Science in Computer Studies", abbreviation = "BSCS")
            )
        )

        val student = studentRepository.save(
            Student(
                name = "Fermin Vismanos Jr",
                email = "fermin@vauldex.com",
                age = 30,
                degree = degrees.first { it.abbreviation.equals("bsit", ignoreCase = true) }
            )
        )

        insertedStudentId = student.id
    }

    @Test
    fun `GET all students and returns 200 OK`()
    {
        mockMvc
            .perform(
            get("/students")
                .cookie(cookie)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.page.totalElements").value(1))
    }

    @Test
    fun `GET find student using id and returns 200 OK`()
    {
        mockMvc
            .perform(
            get("/students/$insertedStudentId")
                .cookie(cookie)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(insertedStudentId))
    }

    @Test
    fun `POST create new student and return 201 CREATED`()
    {
        mockMvc
            .perform(
            post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    StudentRequest(
                        name = "John Doe",
                        email = "johndoe@gmail.com",
                        age = 40,
                        degree = "bsit"
                    )
                ))
                .cookie(cookie)
            )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("johndoe@gmail.com"))
            .andExpect(jsonPath("$.age").value(40))
            .andExpect(jsonPath("$.degree").value("BSIT"))
    }

    @Test
    fun `PUT update student using id and returns 200 OK`()
    {
        mockMvc
            .perform(
            put("/students/$insertedStudentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    StudentRequest(
                        name = "Fermin Vismanos Jr - Updated",
                        email = "ferminvismanosupdated@gmail.com",
                        age = 50,
                        degree = "bscs"
                    )
                ))
                .cookie(cookie)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Fermin Vismanos Jr - Updated"))
            .andExpect(jsonPath("$.email").value("ferminvismanosupdated@gmail.com"))
            .andExpect(jsonPath("$.age").value(50))
            .andExpect(jsonPath("$.degree").value("BSCS"))
    }

    @Test
    fun `DELETE student record using id and return 204 NO CONTENT`()
    {
        mockMvc
            .perform(
            delete("/students/$insertedStudentId")
                .cookie(cookie)
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `Throws an exception if validation fails and returns a 400 BAD REQUEST`()
    {
        mockMvc
            .perform(
            post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    StudentRequest(
                        name = "",
                        email = "",
                        age = 0,
                        degree = ""
                    )
                ))
                .cookie(cookie)
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error.name").value("Name is required"))
            .andExpect(jsonPath("$.error.email").value("Email is required"))
            .andExpect(jsonPath("$.error.degree").value("Degree is required"))
            .andExpect(jsonPath("$.error.age").value("Age must be at least 1"))
    }

    @Test
    fun `Throw StudentNotFoundException using id and returns 404 NOT FOUND`()
    {
        studentService.delete(insertedStudentId)

        mockMvc
            .perform(
            get("/students/$insertedStudentId")
                .cookie(cookie)
            )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Student not found"))
    }

    @Test
    fun `Throw Exception when course is not found and returns 404 NOT FOUND`()
    {
        mockMvc
            .perform(
            post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    StudentRequest(
                        name = "Sample Test",
                        email = "sampletest@gmail.com",
                        age = 40,
                        degree = "bsxyz"
                    )
                ))
                .cookie(cookie)
            )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Degree not found"))
    }

    @Test
    fun `Refuse to go thru if token expired or not added and returns 403 FORBIDDEN`()
    {
        mockMvc
            .perform(
            get("/students")
            )
            .andExpect(status().isForbidden)
    }
}
