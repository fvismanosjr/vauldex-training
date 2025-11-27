package com.example.studentapi.service

import com.example.studentapi.entity.Degree
import com.example.studentapi.entity.Student
import com.example.studentapi.entity.User
import com.example.studentapi.exception.DegreeNotFoundException
import com.example.studentapi.exception.RequestValidationException
import com.example.studentapi.exception.ValidationError
import com.example.studentapi.model.DegreeRequest
import com.example.studentapi.model.DegreeResponse
import com.example.studentapi.model.DegreeResponseWithListOfStudents
import com.example.studentapi.repository.DegreeRepository
import com.example.studentapi.specification.SortByCountSpecification
import com.example.studentapi.specification.StringFilterSpecification
import com.example.studentapi.utils.SanitizeString
import com.example.studentapi.utils.withoutCollectionSort
import org.slf4j.LoggerFactory
import org.springframework.data.domain.AuditorAware
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class DegreeService(
    private val degreeRepository: DegreeRepository,
    private val auditorAware: AuditorAware<User>,
) {

    private val serviceLogger = LoggerFactory.getLogger(Degree::class.java)
    private var validationErrors: MutableList<ValidationError> = mutableListOf()

    private fun findDegreeById(id: Long): Degree {
        return degreeRepository
            .findById(id)
            .orElseThrow { DegreeNotFoundException() }
    }

    fun findAll(
        filter: String? = null,
        pageable: Pageable? = Pageable.unpaged()
    ): Page<DegreeResponse> {
        return degreeRepository.findAll(
            StringFilterSpecification(filter).generate<Degree>()
                .and(SortByCountSpecification(pageable).generate<Degree, Student>()),
            pageable?.withoutCollectionSort() ?: Pageable.unpaged()
        ).map { it.toDegreeResponse() }
    }

    fun find(id: Long): DegreeResponse
    {
        return degreeRepository
            .findById(id)
            .map { it.toDegreeResponse() }
            .orElseThrow { DegreeNotFoundException() }
    }

    fun findByIdWithStudents(id: Long): DegreeResponseWithListOfStudents {
        return degreeRepository
            .findById(id)
            .map { it.toDegreeResponseWithListOfStudents() }
            .orElseThrow { DegreeNotFoundException() }
    }

    fun save(request: DegreeRequest) : DegreeResponse {
        serviceLogger.info("Saving a new degree record")
        validationErrors = mutableListOf()

        request.validateIfDegreeExist()

        validationErrors
            .takeIf { it.isNotEmpty() }
            ?.let { throw RequestValidationException(errors = it) }

        return degreeRepository
            .save(request.toDegree())
            .let { find(it.id) }
    }

    fun update(id: Long, request: DegreeRequest): DegreeResponse
    {
        serviceLogger.info("Updating student record with id:$id")

        request.validateIfDegreeExist()

        return degreeRepository
            .save(
                findDegreeById(id)
                    .apply {
                        this.name = request.name
                        this.abbreviation = request.abbreviation
                    }
            )
            .let { find(it.id) }
    }

    fun delete(id: Long) : Degree {
        serviceLogger.info("Deleting degree record with id:$id")

        return degreeRepository.save(
            findDegreeById(id).apply {
                this.deletedAt = Instant.now()
                this.deletedBy = auditorAware.currentAuditor.orElse(null)
            }
        )
    }

    // validations
    private fun DegreeRequest.validateIfDegreeExist(): DegreeRequest =
        apply {
            degreeRepository
                .existsByAbbreviation(SanitizeString.clean(abbreviation))
                .takeIf { it }
                ?.let {
                    validationErrors.add(ValidationError(
                        field = "abbreviation",
                        message = "Degree with the same abbreviation already exists"
                    ))
                }
        }
}