package com.thomas.spring.boot.filter

import com.thomas.core.context.SessionContextHolder
import com.thomas.logger.log.KotlinLogger
import com.thomas.spring.boot.extension.bearerToken
import com.thomas.spring.boot.extension.clearRequestContext
import com.thomas.spring.boot.extension.withSecurityContext
import com.thomas.spring.boot.token.TokenDecrypter
import io.github.oshai.kotlinlogging.Level.DEBUG
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class AuthenticationFilter(
    private val tokenDecrypter: TokenDecrypter
) : WebFilter, KotlinLogger by KotlinLogger.logger(AuthenticationFilter::class) {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = exchange.request.bearerToken()
        return if (token != null) {
            try {
                val securityUser = tokenDecrypter.decrypt(token)
                val context = SessionContextHolder.withSecurityContext(securityUser, token)
                chain.filter(exchange).contextWrite(context)
            } catch (e: Exception) {
                at(DEBUG, exception = e) { "Error decrypting token $token: ${e.message ?: "No detail available"}" }
                SessionContextHolder.clearRequestContext()
                throw e
            }
        } else {
            SessionContextHolder.clearRequestContext()
            chain.filter(exchange)
        }
    }

}
