package com.thomas.spring.boot.filter

import com.thomas.core.context.SessionContextHolder.scopeIdentifier
import com.thomas.core.context.SessionContextHolder.traceIdentifier
import com.thomas.logger.log.KotlinLogger
import com.thomas.spring.boot.extension.traceIdentifier
import org.slf4j.MDC
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class TraceIdentifierFilter : WebFilter, KotlinLogger by KotlinLogger.logger(TraceIdentifierFilter::class) {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val identifier = exchange.request.traceIdentifier()
        debug { "Trace identifier received: $identifier" }
        identifier?.apply { traceIdentifier = this }
        MDC.put("traceId", traceIdentifier)
        MDC.put("scopeId", scopeIdentifier)
        return chain.filter(exchange)
    }

}
