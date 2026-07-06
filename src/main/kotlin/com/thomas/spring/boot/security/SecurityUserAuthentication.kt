package com.thomas.spring.boot.security

import com.thomas.core.model.security.SecurityUser
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class SecurityUserAuthentication(
    private val securityUser: SecurityUser,
    private val requestToken: String,
    private var authenticatedUser: Boolean,
) : Authentication {

    private val authorities: MutableSet<GrantedAuthority> = securityUser.currentRoles.map {
        SimpleGrantedAuthority(it.role)
    }.toMutableSet()

    override fun getName(): String = securityUser.userId.toString()

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities

    override fun getCredentials(): Any = requestToken

    override fun getDetails(): Any = securityUser

    override fun getPrincipal(): Any = securityUser

    override fun isAuthenticated(): Boolean = authenticatedUser

    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticatedUser = isAuthenticated
    }

}
