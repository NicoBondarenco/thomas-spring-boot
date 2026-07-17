package com.thomas.spring.boot.filter

import com.thomas.core.context.SessionContextHolder
import com.thomas.logger.log.KotlinLogger
import com.thomas.spring.boot.extension.clearRequestContext
import org.slf4j.MDC
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class SessionContextLifecycleFilter : WebFilter, KotlinLogger by KotlinLogger.logger(SessionContextLifecycleFilter::class) {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> = try {
        chain.filter(exchange)
    } finally {
        debug { "Clearing context" }
        MDC.clear()
        SessionContextHolder.clearRequestContext()
    }

}
