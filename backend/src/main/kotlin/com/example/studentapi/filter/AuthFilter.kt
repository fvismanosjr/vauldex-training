package com.example.studentapi.filter

import com.example.studentapi.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Skip WebSocket handshake
        if (request.servletPath.startsWith("/ws-chat")) {
            filterChain.doFilter(request, response)
            return
        }

        request.cookies
            ?.find { it.name == "access_token" }
            ?.takeIf { it.value.isNotEmpty() }
            ?.takeIf { SecurityContextHolder.getContext().authentication == null }
            ?.let {
                runCatching { jwtService.extractAndFindUsername(it.value) }
                    .getOrNull()
                    ?.also { user ->
                        UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            listOf(SimpleGrantedAuthority("ROLE_USER"))
                        ).also { auth ->
                            auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                            SecurityContextHolder.getContext().authentication = auth
                        }
                    }
                    ?: jwtService.clearCookie(response)
            }

        filterChain.doFilter(request, response)
    }
}