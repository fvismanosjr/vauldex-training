package com.example.studentapi.service

import com.example.studentapi.entity.Degree
import com.example.studentapi.entity.Student
import com.example.studentapi.exception.DegreeNotFoundException
import com.example.studentapi.exception.RequestValidationException
import com.example.studentapi.exception.StudentNotFoundException
import com.example.studentapi.model.StudentRequest
import com.example.studentapi.repository.DegreeRepository
import com.example.studentapi.repository.StudentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import kotlin.properties.Delegates

@SpringBootTest
@Testcontainers
class StudentServiceTest {
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
    lateinit var studentService: StudentService

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var degreeRepository: DegreeRepository

    private var insertedStudentId: Long by Delegates.notNull()

    @BeforeEach
    @Transactional
    fun setup()
    {
        degreeRepository.deleteAll()
        studentRepository.deleteAll()

        // add degrees
        val degrees = degreeRepository.saveAll(
            listOf(
                Degree(name = "Bachelor of Science in Information Technology", abbreviation = "BSIT"),
                Degree(name = "Bachelor of Science in Computer Studies", abbreviation = "BSCS")
            )
        )

        // add 1 student record
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
    fun `should get all students`()
    {
        // when
        val students = studentService.findAll()

        // then
        assertEquals(1, students.size)
    }

    @Test
    fun `should get student using student id`()
    {
        // when
        val student = studentService.find(insertedStudentId)

        // then
        assertEquals("fermin@vauldex.com", student.email)
    }

    @Test
    fun `should save a student`()
    {
        // given
        val savedStudent = studentService.save(
            StudentRequest(
                name = "John Doe",
                email = "john@doe.com",
                age = 40,
                degree = "bsit"
            )
        )

        // when
        val fetchStudent = studentService.find(savedStudent.id)

        // then
        assertEquals("john@doe.com", fetchStudent.email)
        assertEquals("bsit".lowercase(), fetchStudent.degree?.lowercase())
    }

    @Test
    fun `should update student using student id`()
    {
        // given
        val updatedStudent = studentService.update(
            insertedStudentId,
            StudentRequest(
                name = "Fermin Vismanos Jr - Updated",
                email = "fermin-updated@vauldex.com",
                age = 45,
                degree = "bscs"
            )
        )

        // when
        val fetchStudent = studentService.find(updatedStudent.id)

        // then
        assertEquals("fermin-updated@vauldex.com", fetchStudent.email)
        assertEquals("Fermin Vismanos Jr - Updated", fetchStudent.name)
    }

    @Test
    fun `should delete student using student id`()
    {
        // when
        studentService.delete(insertedStudentId)

        // when
        val deletedUser = studentRepository.findByIdIncludingTrash(insertedStudentId)

        // then
        assertNotNull(deletedUser?.deletedAt)
    }

    @Test
    fun `show throw Exception if email already exists`()
    {
        // given
        val savedStudent = studentService.save(
            StudentRequest(
                name = "John Doe",
                email = "john@doe.com",
                age = 40,
                degree = "bsit"
            )
        )

        // when then
        val exception = assertThrows<RequestValidationException> {
            studentService.save(
                StudentRequest(
                    name = "John Doe II",
                    email = "john@doe.com",
                    age = 40,
                    degree = "bsit"
                )
            )
        }

        // then
        assert(exception.errors.toString().contains("Email already exists"))
    }

    @Test
    fun `should throw Exception if course is not found`()
    {
        // when then
        val exception = assertThrows<DegreeNotFoundException> {
            studentService.save(
                StudentRequest(
                    name = "John Doe II",
                    email = "john2@doe.com",
                    age = 40,
                    degree = "bsba"
                )
            )
        }

        assert(exception.message.toString().contains("Degree not found"))
    }

    @Test
    fun `should throw Exception if student is not found`()
    {
        // when then
        val exception = assertThrows<StudentNotFoundException> {
            studentService.find(999L)
        }

        assert(exception.message!!.contains("Student not found"))
    }
}