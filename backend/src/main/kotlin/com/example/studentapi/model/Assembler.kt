package com.example.studentapi.model

import com.example.studentapi.controller.DegreeController
import com.example.studentapi.controller.StudentController
import com.example.studentapi.model.StudentResponse
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class StudentAssembler : RepresentationModelAssembler<StudentResponse, EntityModel<StudentResponse>> {
    override fun toModel(entity: StudentResponse) : EntityModel<StudentResponse> =
        EntityModel.of(
            entity,
            linkTo(methodOn(StudentController::class.java).find(entity.id)).withSelfRel()
        )
}

@Component
class DegreeAssembler : RepresentationModelAssembler<DegreeResponse, EntityModel<DegreeResponse>> {
    override fun toModel(entity: DegreeResponse) : EntityModel<DegreeResponse> =
        EntityModel.of(
            entity,
            linkTo(methodOn(DegreeController::class.java).find(entity.id)).withSelfRel()
        )
}

@Component
class DegreeWithStudentAssembler : RepresentationModelAssembler<DegreeResponseWithListOfStudents, EntityModel<DegreeResponseWithListOfStudents>> {
    override fun toModel(entity: DegreeResponseWithListOfStudents) : EntityModel<DegreeResponseWithListOfStudents> =
        EntityModel.of(
            entity,
            linkTo(methodOn(DegreeController::class.java).find(entity.id)).withSelfRel()
        )
}