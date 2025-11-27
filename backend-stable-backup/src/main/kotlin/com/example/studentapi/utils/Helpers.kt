package com.example.studentapi.utils

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

fun Pageable.withoutCollectionSort(): Pageable =
    sort.filterNot { it.property.startsWith("noOf") }
        .takeIf { it.isNotEmpty() }
        ?.let { PageRequest.of(pageNumber, pageSize, Sort.by(it)) }
        ?: PageRequest.of(pageNumber, pageSize, Sort.unsorted())

