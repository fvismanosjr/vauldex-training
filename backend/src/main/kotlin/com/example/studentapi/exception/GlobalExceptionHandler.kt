package com.example.studentapi.exception

import com.example.studentapi.service.StudentService
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatusCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.MethodArgumentNotValidException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException

data class ValidationError(
    val field: String,
    val message: String,
)

sealed class BaseException(
    val code: HttpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
    message: String = "Internal Server Error",
) : RuntimeException(message)

data class RequestValidationException(
    val code: HttpStatusCode = HttpStatus.BAD_REQUEST,
    val errors: MutableList<ValidationError>,
    override val message: String = "Input Validation Exception",
) : RuntimeException(message)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseExceptionResponse(
    val code: Int,
    val type: HttpStatusCode,
    val message: String,
    val error: Map<String, String?>? = null
)

@ControllerAdvice
class GlobalExceptionHandler
{
    private final val exceptionLogger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(value = [
        BaseException::class,
        MethodArgumentNotValidException::class,
        DataIntegrityViolationException::class,
        RequestValidationException::class,
        Exception::class
    ])
    fun handleExceptions(exception: Exception): ResponseEntity<BaseExceptionResponse>
    {
        var status: HttpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR
        var message: String? = exception.message ?: ""
        var errors: Map<String, String?>? = null

        when (exception) {
            is BaseException -> {
                status = exception.code
                message = exception.message
                exceptionLogger.warn(message)
            }
            is MethodArgumentNotValidException -> {
                status = HttpStatus.BAD_REQUEST
                errors = exception.fieldErrors.associate { it.field to it.defaultMessage }
                exceptionLogger.warn(message)
            }
            is RequestValidationException -> {
                status = exception.code
                errors = exception.errors.associate { it.field to it.message }
                exceptionLogger.warn(message)
            }
            is DataIntegrityViolationException -> {
                message = exception.mostSpecificCause.message?.substringAfter("Detail: ")?.trim() ?: "Constraint violation occurred"
                exceptionLogger.error(message)
            }
            else -> {
                exceptionLogger.error(message, exception)
            }
        }

        return ResponseEntity.status(status).body(BaseExceptionResponse(
            code = status.value(),
            type = status,
            message = message ?: "Something went wrong! Try again later.",
            error = errors
        ))
    }
}

class StudentNotFoundException : BaseException(code = HttpStatus.NOT_FOUND, message = "Student not found")
class DegreeNotFoundException : BaseException(code = HttpStatus.NOT_FOUND, message = "Degree not found")
class UserNotFoundException : BaseException(code = HttpStatus.NOT_FOUND, message = "User not found")
class UserAlreadyExistException : BaseException(code = HttpStatus.BAD_REQUEST, message = "User already exists")
class UnauthorizedException: BaseException(code = HttpStatus.UNAUTHORIZED, message = "Unauthorized access")
