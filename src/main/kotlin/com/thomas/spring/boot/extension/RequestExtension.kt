package com.thomas.spring.boot.extension

import com.thomas.core.extension.UUID_REGEX
import jakarta.servlet.http.HttpServletRequest
import java.util.UUID
import org.springframework.http.HttpHeaders.AUTHORIZATION

private const val BEARER_TOKEN_PREFIX = "Bearer "
private const val UNITY_HEADER_TOKEN = "Current-Unity"
private const val LINK_HEADER_TOKEN = "Link-Identifier"

fun HttpServletRequest.bearerToken(): String? = this.headerValue(AUTHORIZATION) {
    it.startsWith(BEARER_TOKEN_PREFIX)
}?.substring(BEARER_TOKEN_PREFIX.length)

fun HttpServletRequest.currentUnity(): UUID? = this.headerValue(UNITY_HEADER_TOKEN) {
    UUID_REGEX.matches(it)
}?.let { UUID.fromString(it) }

fun HttpServletRequest.linkIdentifier(): String? = this.headerValue(LINK_HEADER_TOKEN)

private fun HttpServletRequest.headerValue(name: String, valid: (String) -> Boolean = { true }): String? {
    val header = this.getHeader(name)
    if (header == null || header.trim().isEmpty() || !valid(header)) {
        return null
    }
    return header.trim()
}
