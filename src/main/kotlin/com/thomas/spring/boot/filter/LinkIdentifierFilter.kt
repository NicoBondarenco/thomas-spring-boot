package com.thomas.spring.boot.filter

import com.thomas.core.context.SessionContextHolder.linkIdentifier
import com.thomas.logger.log.KotlinLogger
import com.thomas.spring.boot.extension.linkIdentifier
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class LinkIdentifierFilter : OncePerRequestFilter(), KotlinLogger by KotlinLogger.logger(LinkIdentifierFilter::class) {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val identifier = request.linkIdentifier()
        debug { "Link identifier received: $identifier" }
        identifier?.apply { linkIdentifier = this }
        filterChain.doFilter(request, response)
    }

}
