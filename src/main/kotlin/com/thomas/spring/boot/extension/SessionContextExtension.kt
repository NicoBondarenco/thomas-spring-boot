package com.thomas.spring.boot.extension

import com.thomas.core.context.SessionContextHolder
import com.thomas.core.context.SessionContextHolder.updateContext
import com.thomas.core.model.security.SecurityUser
import com.thomas.spring.boot.security.SecurityUserAuthentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder.getContext
import reactor.util.context.Context

fun SessionContextHolder.clearRequestContext() {
    this.clearContext()
    SecurityContextHolder.clearContext()
    ReactiveSecurityContextHolder.clearContext()
}

fun SessionContextHolder.withSecurityContext(user: SecurityUser, token: String): Context = updateContext {
    it.copy(user = user, token = token, currentOrganization = user.organizationId)
}.let {
    val authentication = SecurityUserAuthentication(user, token, true)
    getContext().authentication = authentication
    withAuthentication(authentication)
}
