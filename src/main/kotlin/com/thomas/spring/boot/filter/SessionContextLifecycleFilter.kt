package com.thomas.spring.boot.filter

import com.thomas.core.context.SessionContextHolder.clearContext
import com.thomas.logger.log.KotlinLogger
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class SessionContextLifecycleFilter : OncePerRequestFilter(), KotlinLogger by KotlinLogger.logger(SessionContextLifecycleFilter::class) {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } finally {
            debug { "Clearing context" }
            clearContext()
        }
    }

}
