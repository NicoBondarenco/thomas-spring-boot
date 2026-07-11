package com.thomas.spring.boot.filter

import com.thomas.core.context.SessionContextHolder.traceIdentifier
import com.thomas.logger.log.KotlinLogger
import com.thomas.spring.boot.extension.traceIdentifier
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class TraceIdentifierFilter : OncePerRequestFilter(), KotlinLogger by KotlinLogger.logger(TraceIdentifierFilter::class) {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val identifier = request.traceIdentifier()
        debug { "Trace identifier received: $identifier" }
        identifier?.apply { traceIdentifier = this }
        filterChain.doFilter(request, response)
    }

}
