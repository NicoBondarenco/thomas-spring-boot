package com.thomas.spring.boot.extension

import com.thomas.core.extension.UUID_REGEX
import com.thomas.core.extension.substringTrimmed
import java.util.Locale
import java.util.UUID
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.server.reactive.ServerHttpRequest

const val BEARER_TOKEN_PREFIX = "Bearer"
const val UNITY_HEADER_TOKEN = "Current-Unity"
const val TRACE_HEADER_TOKEN = "Trace-Identifier"

fun ServerHttpRequest.bearerToken(): String? = this.headerValue(AUTHORIZATION) {
    it.startsWith(BEARER_TOKEN_PREFIX)
}?.substringTrimmed(BEARER_TOKEN_PREFIX.length)

fun ServerHttpRequest.currentUnity(): UUID? = this.headerValue(UNITY_HEADER_TOKEN) {
    UUID_REGEX.matches(it)
}?.let { UUID.fromString(it) }

fun ServerHttpRequest.traceIdentifier(): String? = this.headerValue(TRACE_HEADER_TOKEN)

fun ServerHttpRequest.primaryLocale(): Locale = this.headers.acceptLanguageAsLocales.firstOrNull() ?: Locale.ROOT

private fun ServerHttpRequest.headerValue(name: String, valid: (String) -> Boolean = { true }): String? {
    val header = this.headers[name]?.firstOrNull()
    if (header == null || header.trim().isEmpty() || !valid(header)) {
        return null
    }
    return header.trim()
}
