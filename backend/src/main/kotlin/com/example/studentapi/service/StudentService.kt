package com.example.studentapi.service

import com.example.studentapi.entity.Degree
import com.example.studentapi.entity.Student
import com.example.studentapi.entity.User
import com.example.studentapi.model.StudentRequest
import com.example.studentapi.model.StudentResponse
import com.example.studentapi.exception.DegreeNotFoundException
import com.example.studentapi.exception.RequestValidationException
import com.example.studentapi.exception.StudentNotFoundException
import com.example.studentapi.exception.ValidationError
import com.example.studentapi.repository.DegreeRepository
import com.example.studentapi.repository.StudentRepository
import com.example.studentapi.specification.StringFilterSpecification
import com.example.studentapi.utils.SanitizeString
import org.slf4j.LoggerFactory
import org.springframework.data.domain.AuditorAware
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val degreeRepository: DegreeRepository,
    private val auditorAware: AuditorAware<User>,
) {

    private val serviceLogger = LoggerFactory.getLogger(StudentService::class.java)
    private var validationErrors: MutableList<ValidationError> = mutableListOf()

    private fun findStudentById(id: Long): Student {
        return studentRepository
            .findById(id)
            .orElseThrow { StudentNotFoundException() }
    }

    private fun findDegreeByAbbreviation(degree: String) : Degree {
        return degreeRepository
            .findByAbbreviationIgnoreCase(SanitizeString.clean(degree))
            ?: throw DegreeNotFoundException()
    }

    fun findAll(
        filter: String? = null,
        pageable: Pageable? = Pageable.unpaged()
    ) : Page<StudentResponse> {
        serviceLogger.info("Fetching all students")

        return studentRepository.findAll(
            StringFilterSpecification(filter).generate(),
            pageable ?: Pageable.unpaged()
        ).map { it.toStudentResponse() }
    }

    fun find(id: Long) : StudentResponse {
        serviceLogger.info("Fetching student record with id:$id")

        return studentRepository
            .findById(id)
            .map { it.toStudentResponse() }
            .orElseThrow { StudentNotFoundException() }
    }

    @Transactional
    fun save(request: StudentRequest) : StudentResponse {
        serviceLogger.info("Saving a new student record")
        validationErrors = mutableListOf()

        // business logic validation
        request
        .validateIfEmailExist()
        .validateIfDegreeExist()

        validationErrors
        .takeIf { it.isNotEmpty() }
        ?.let { throw RequestValidationException(errors = it) }

        return studentRepository
            .save(request.toEntity(findDegreeByAbbreviation(request.degree)))
            .let { find(it.id) }
    }

    @Transactional
    fun update(id: Long, request: StudentRequest): StudentResponse
    {
        serviceLogger.info("Updating student record with id:$id")

        request
            .validateIfEmailExistAndIdNot(id)
            .validateIfDegreeExist()

        return studentRepository
            .save(
                findStudentById(id)
                    .apply {
                        this.name = request.name
                        this.email = request.email
                        this.birthdate = request.birthdate ?: LocalDate.now()
                        this.degree = findDegreeByAbbreviation(request.degree)
                    }
            )
            .let { find(it.id) }
    }

    @Transactional
    fun delete(id: Long) : Student {
        serviceLogger.info("Deleting student record with id:$id")

        return studentRepository.save(
            findStudentById(id).apply {
                this.deletedAt = Instant.now()
                this.deletedBy = auditorAware.currentAuditor.orElse(null)
            }
        )
    }

    // validations
    private fun StudentRequest.validateIfEmailExist(): StudentRequest =
        apply {
            studentRepository
                .existsByEmail(SanitizeString.clean(email))
                .takeIf { it }
                ?.let {
                    validationErrors.add(ValidationError(
                        field = "email",
                        message = "Email already exists"
                    ))
                }
            }

    private fun StudentRequest.validateIfEmailExistAndIdNot(id: Long): StudentRequest =
        apply {
            studentRepository
                .existsByEmailAndIdNot(SanitizeString.clean(email), id)
                .takeIf { it }
                ?.let {
                    validationErrors.add(ValidationError(
                        field = "email",
                        message = "Email already exists"
                    ))
                }
        }

    private fun StudentRequest.validateIfDegreeExist(): StudentRequest = apply { findDegreeByAbbreviation(degree) }
}