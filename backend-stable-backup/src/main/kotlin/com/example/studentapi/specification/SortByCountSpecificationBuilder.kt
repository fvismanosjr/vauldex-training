package com.example.studentapi.specification

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import jakarta.persistence.criteria.*

class SortByCountSpecification(val pageable: Pageable?)
{
    fun <T, C> generate(): Specification<T>
    {
        return Specification { root: Root<T>, query: CriteriaQuery<*>?, cb: CriteriaBuilder ->
            pageable?.sort
                ?.firstOrNull { it.property.startsWith("noOf") }
                ?.let { sortOrder ->
                    val collectionField = sortOrder.property
                        .removePrefix("noOf")
                        .replaceFirstChar { it.lowercase() }

                    val join: Join<T, C> = root.join(collectionField, JoinType.LEFT)

                    query?.groupBy(root)
                    query?.orderBy(
                        when (sortOrder.direction) {
                            Sort.Direction.ASC -> cb.asc(cb.count(join))
                            Sort.Direction.DESC -> cb.desc(cb.count(join))
                        }
                    )
                }

            // No additional filtering, so just return conjunction
            cb.conjunction()
        }
    }
}
