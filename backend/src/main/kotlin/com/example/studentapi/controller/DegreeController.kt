package com.example.studentapi.controller

import com.example.studentapi.model.DegreeAssembler
import com.example.studentapi.model.DegreeRequest
import com.example.studentapi.model.DegreeResponse
import com.example.studentapi.model.DegreeWithStudentAssembler
import com.example.studentapi.service.DegreeService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/degrees")
class DegreeController(
    private val degreeService: DegreeService,
    private val degreeAssembler: DegreeAssembler,
    private val degreeWithStudentAssembler: DegreeWithStudentAssembler,
    private val pagedResourcesAssembler: PagedResourcesAssembler<DegreeResponse>
) {
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
    ) = pagedResourcesAssembler.toModel(
        degreeService.findAll(filter, pageable),
        degreeAssembler
    )

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun find(
        @PathVariable id: Long
    ) = degreeAssembler.toModel(degreeService.find(id))

    @GetMapping("/{id}/students")
    @ResponseStatus(HttpStatus.OK)
    fun findWithStudents(
        @PathVariable id: Long
    ) = degreeWithStudentAssembler.toModel(degreeService.findByIdWithStudents(id))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun store(
        @Valid @RequestBody request: DegreeRequest
    ) = degreeAssembler.toModel(degreeService.save(request))

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: DegreeRequest
    ) = degreeAssembler.toModel(degreeService.update(id, request))

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun destroy(@PathVariable id: Long) = degreeService.delete(id)
}