package com.example.studentapi.repository

import com.example.studentapi.entity.Degree
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface DegreeRepository: JpaRepository<Degree, Long>, JpaSpecificationExecutor<Degree> {
    fun findByNameIgnoreCase(name: String): Degree?
    fun findByAbbreviationIgnoreCase(name: String): Degree?
    fun existsByAbbreviation(abbreviation: String): Boolean
}