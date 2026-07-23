package com.thomas.spring.boot.filter

import com.thomas.core.context.SessionContext
import com.thomas.core.context.SessionContextHolder
import com.thomas.logger.log.KotlinLogger
import com.thomas.spring.boot.extension.bearerToken
import com.thomas.spring.boot.extension.sessionContext
import com.thomas.spring.boot.extension.withSecurityContext
import com.thomas.spring.boot.extension.withSessionContext
import com.thomas.spring.boot.security.SecurityUserAuthentication
import com.thomas.spring.boot.token.TokenDecrypter
import io.github.oshai.kotlinlogging.Level.DEBUG
import org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication
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
                chain.filter(exchange).contextWrite { context ->
                    val currentSession = context.sessionContext()
                    val updatedSession = currentSession.copy(user = securityUser, token = token)
                    val authentication = SecurityUserAuthentication(securityUser, token, true)
                    val contextWithSession = context.withSessionContext(updatedSession)
                    contextWithSession.putAll(withAuthentication(authentication).readOnly())
                }
            } catch (e: Exception) {
                at(DEBUG, exception = e) { "Error decrypting token $token: ${e.message ?: "No detail available"}" }
                Mono.error(e)
            }
        } else {
            chain.filter(exchange)
        }
    }

}
