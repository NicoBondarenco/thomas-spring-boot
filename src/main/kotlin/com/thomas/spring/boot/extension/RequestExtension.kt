package com.thomas.spring.boot.extension

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders.AUTHORIZATION

private const val BEARER_TOKEN_PREFIX = "Bearer "

fun HttpServletRequest.bearerToken(): String? {
    val header = this.getHeader(AUTHORIZATION)
    if (header == null || header.trim().isEmpty() || !header.startsWith(BEARER_TOKEN_PREFIX)) {
        return null
    }
    return header.substring(BEARER_TOKEN_PREFIX.length)
}
