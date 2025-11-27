package com.example.studentapi.repository

import com.example.studentapi.entity.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository: JpaRepository<Student, Long>, JpaSpecificationExecutor<Student>
{
    fun existsByEmail(email: String): Boolean
    fun existsByEmailAndIdNot(email: String, id: Long): Boolean
    fun findByEmail(email: String): Student?

    @Query("SELECT * FROM students WHERE id = :id", nativeQuery = true)
    fun findByIdIncludingTrash(@Param("id") id: Long): Student?
}