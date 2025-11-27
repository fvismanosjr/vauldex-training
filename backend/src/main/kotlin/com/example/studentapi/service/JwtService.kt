package com.example.studentapi.service

import com.example.studentapi.entity.User
import com.example.studentapi.exception.UserNotFoundException
import com.example.studentapi.repository.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    private val userRepository: UserRepository,
) {

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.expiration}")
    private lateinit var expiration: String

    private val secretKey: SecretKey by lazy { Keys.hmacShaKeyFor(secret.toByteArray()) }

    fun extractAndFindUsername(token: String): User
    {
        val username = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
            ?: throw BadCredentialsException("Invalid token")

        return userRepository
            .findByUsername(username)
            ?: throw UserNotFoundException()
    }

    fun generateToken(username: String): String
    {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date())
                .setExpiration(Date(Date().time + expiration.toInt()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact()
    }

    fun generateCookie(token: String) = Cookie("access_token", token).apply {
        isHttpOnly = true
        secure = false
        path = "/"
        maxAge = expiration.toInt()
    }

    fun clearCookie(response: HttpServletResponse)
    {
        Cookie("access_token", null).apply {
            isHttpOnly = true
            secure = false
            path = "/"
            maxAge = 0
        }.let { response.addCookie(it) }
    }
}