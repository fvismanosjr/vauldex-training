package com.example.studentapi.controller

import com.example.studentapi.model.StudentAssembler
import com.example.studentapi.model.StudentRequest
import com.example.studentapi.model.StudentResponse
import com.example.studentapi.service.StudentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.validation.annotation.Validated
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedResourcesAssembler

@RestController
@Validated
@RequestMapping("/students")
class StudentController(
    private val studentService: StudentService,
    private val studentAssembler: StudentAssembler,
    private val pageResourceAssembler: PagedResourcesAssembler<StudentResponse>,
) {
    @Operation(
        summary = "Retrieve all student records with degree programs",
        description = "Fetches all student records and their corresponding degree, applying any specified filters and pagination",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful retrieval of student records"),
            ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun findAll(
        @RequestParam(required = false, name = "filter") filter: String? = null,
        @PageableDefault(
            page = 0,
            size = 20,
            sort = ["id"],
            direction = Sort.Direction.ASC
        ) pageable: Pageable? = Pageable.unpaged()
    ) = pageResourceAssembler.toModel(
        studentService.findAll(filter, pageable),
        studentAssembler
    )

    @Operation(
        summary = "Retrieve a single student by ID",
        description = "Fetches a student record by its unique ID along with the enrolled degree program."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Student found"),
            ApiResponse(responseCode = "404", description = "Student not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun find(
        @Parameter(description = "ID of the student to retrieve")
        @PathVariable id: Long
    ) = studentAssembler.toModel(studentService.find(id))

    @Operation(
        summary = "Create a new student record",
        description = "Adds a new student along with their enrolled degree program."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Student successfully created"),
            ApiResponse(responseCode = "400", description = "Invalid request body"),
            ApiResponse(responseCode = "404", description = "Degree not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun store(
        @Parameter(description = "Student details to create")
        @Valid @RequestBody request: StudentRequest
    ) = studentAssembler.toModel(studentService.save(request))

    @Operation(
        summary = "Update an existing student record",
        description = "Updates the details of a student by ID including their degree program."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Student successfully updated"),
            ApiResponse(responseCode = "400", description = "Invalid request body"),
            ApiResponse(responseCode = "404", description = "Student or degree not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(
        @Parameter(description = "ID of the student to update")
        @PathVariable id: Long,
        @Parameter(description = "Updated student details")
        @Valid @RequestBody request: StudentRequest
    ) = studentAssembler.toModel(studentService.update(id, request))

    @Operation(
        summary = "Delete a student record",
        description = "Removes a student from the system by ID."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Student successfully deleted"),
            ApiResponse(responseCode = "404", description = "Student not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun destroy(
        @Parameter(description = "ID of the student to delete")
        @PathVariable id: Long
    ) = studentService.delete(id)
}