package com.thomas.spring.boot.filter

import com.thomas.core.context.SessionContextHolder.updateContext
import com.thomas.core.model.security.SecurityUser
import com.thomas.logger.log.KotlinLogger
import com.thomas.spring.boot.extension.bearerToken
import com.thomas.spring.boot.security.SecurityUserAuthentication
import com.thomas.spring.boot.token.TokenDecrypter
import io.github.oshai.kotlinlogging.Level.DEBUG
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder.getContext
import org.springframework.web.filter.OncePerRequestFilter

class AuthenticationFilter(
    private val tokenDecrypter: TokenDecrypter
) : OncePerRequestFilter(), KotlinLogger by KotlinLogger.logger(AuthenticationFilter::class) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val token = request.bearerToken()
        if (token != null) {
            try {
                val securityUser = tokenDecrypter.decrypt(token)
                updateSecurityContext(securityUser, token)
            } catch (e: Exception) {
                at(DEBUG, exception = e) { "Error decrypting token $token: ${e.message ?: "No detail available"}" }
                clearSecurityContext()
                throw e
            }
        } else {
            clearSecurityContext()
        }
        filterChain.doFilter(request, response)
    }

    private fun updateSecurityContext(user: SecurityUser, token: String) {
        updateContext { it.copy(user = user, token = token, currentOrganization = user.organizationId) }
        getContext().authentication = SecurityUserAuthentication(user, token, true)
    }

    private fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
        updateContext { it.copy(user = null, token = null, currentOrganization = null) }
    }

}
