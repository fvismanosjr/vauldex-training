package com.example.studentapi.controller

import com.example.studentapi.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/users")
class UserController(
    private val userService: UserService
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
    ) = userService.findAll(filter, pageable)

    @GetMapping("/{id}")
    fun find() {}

    @PostMapping
    fun store() {}

    @PutMapping("/{id}")
    fun update() {}

    @DeleteMapping("/{id}")
    fun destroy() {}
}