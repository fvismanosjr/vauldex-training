package com.example.studentapi.specification

import com.example.studentapi.entity.Student
import jakarta.persistence.criteria.*
import org.springframework.data.jpa.domain.Specification

class StringFilterSpecification(var filter: String?)
{
    private val arrayOfOperationalDelimiter = arrayOf("=", "<>", ">", ">=", "<", "<=", "like")
    private val arrayOfConditionalDelimiter = arrayOf("and", "or")

    /*
    * Parse string to map key and value and filter it dynamically using specification api
    * @syntax key<no space>operational<no space>value<space>conditional<space>...
    * @param  name=value
    *
    * operational values includes [=, !=, >, <, >=, <=, like]
    * conditional values includes [and, or]
    *
    * @return Specification
    * */
    fun <T> generate(): Specification<T>
    {
        val splitByCondition = filter
            ?.trim()
            ?.replace("\\s*(and|or)\\s*$".toRegex(), "")
            ?.split("(?=\\s(?:and|or)\\s)".toRegex())
            ?.map(String::trim)
            ?.filter(String::isNotEmpty)
            ?: emptyList()

        var combinedSpec: Specification<T>? = null

        for (part in splitByCondition) {
            val operation = part
                .split(*arrayOfConditionalDelimiter, limit = 2)
                .map(String::trim)
                .firstOrNull { it.isNotEmpty() }

            val condition = arrayOfConditionalDelimiter.firstOrNull { part.startsWith(it) }
            val spec = parseOperationFilter<T>(operation ?: "")

            combinedSpec = when {
                combinedSpec == null -> spec
                condition.equals("or", true) -> combinedSpec.or(spec)
                else -> combinedSpec.and(spec)
            }
        }

        return combinedSpec ?: Specification<T> { _, _, criteria -> criteria.conjunction() }
    }

    private fun <T> parseOperationFilter(filterPart: String) : Specification<T>
    {
        val operator = arrayOfOperationalDelimiter.firstOrNull { filterPart.contains(it, ignoreCase = true) } ?: "="
        val (field, value) = filterPart.split(operator, limit = 2).map(String::trim).let {
            it.getOrNull(0).orEmpty() to it.getOrNull(1).orEmpty()
        }

        return Specification { root, _, cb ->
            root.get<String>(field).let {
                operatorPredicates[operator]
                    ?.invoke(cb, it, value)
                    ?: cb.conjunction()
            }
        }
    }

    private val operatorPredicates: Map<String, (CriteriaBuilder, Expression<String>, String) -> Predicate> = mapOf(
        "=" to { cb, path, value -> cb.equal(path, value) },
        "<>" to { cb, path, value -> cb.notEqual(path, value) },
        ">" to { cb, path, value -> cb.greaterThan(path, value) },
        ">=" to { cb, path, value -> cb.greaterThanOrEqualTo(path, value) },
        "<" to { cb, path, value -> cb.lessThan(path, value) },
        "<=" to { cb, path, value -> cb.lessThanOrEqualTo(path, value) },
        "like" to { cb, path, value -> cb.like(cb.lower(path), "%${value.lowercase()}%") }
    )
}
