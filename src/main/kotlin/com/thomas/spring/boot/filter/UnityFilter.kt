package com.thomas.spring.boot.filter

import com.thomas.core.context.SessionContextHolder.currentUnity
import com.thomas.logger.log.KotlinLogger
import com.thomas.spring.boot.extension.currentUnity
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class UnityFilter : WebFilter, KotlinLogger by KotlinLogger.logger(UnityFilter::class) {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestUnity = exchange.request.currentUnity()
        debug { "Current unity: $requestUnity" }
        currentUnity = requestUnity
        return chain.filter(exchange)
    }

}
