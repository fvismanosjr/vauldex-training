package com.example.studentapi.controller

import com.example.studentapi.model.LoginRequest
import com.example.studentapi.model.LoginResponse
import com.example.studentapi.model.RegisterRequest
import com.example.studentapi.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/auth")
class AuthController (
  private val authService: AuthService
) {
    @Operation(
        summary = "Register a user",
        description = "Register a user's username and password",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User successfully register"),
            ApiResponse(responseCode = "500", description = "User with username provided already exists")
        ]
    )
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    fun register(@RequestBody request: RegisterRequest) = authService.registerUser(request)

    @Operation(
        summary = "Authenticate a user",
        description = "Authenticate a user using username and password"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User successfully authenticated"),
            ApiResponse(responseCode = "400", description = "User not found"),
            ApiResponse(responseCode = "500", description = "User credentials not found")
        ]
    )
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): LoginResponse
    {
        return authService.authenticateUser(request, response)
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(response: HttpServletResponse)
    {
        return authService.unAuthenticateUser(response)
    }

    @Operation(
        summary = "Fetch user data",
        description = "Fetch current logged in user"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Fetching was successful"),
            ApiResponse(responseCode = "400", description = "User not found"),
            ApiResponse(responseCode = "401", description = "Invalid credentials"),
            ApiResponse(responseCode = "403", description = "No access"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    fun me(authentication: Authentication?) = "You are logged in"
}