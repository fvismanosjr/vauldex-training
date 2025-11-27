package com.example.studentapi.interceptor
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class QueryParamValidationInterceptor : HandlerInterceptor
{
    private val allowedParams = setOf("page", "size", "sort", "filter")

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean
    {
        val queryParams = request.parameterMap

        // Check for multiple instances
        queryParams.entries
            .firstOrNull { (_, values) -> values.size > 1 }
            ?.also { (param, _) ->
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Multiple instances of query parameter '$param' are not allowed.")
                return false
            }

        // Check for unexpected parameters
        queryParams.keys
            .firstOrNull { param -> param !in allowedParams }
            ?.also { param ->
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unexpected query parameter '$param'.")
                return false
            }

        return true
    }
}


