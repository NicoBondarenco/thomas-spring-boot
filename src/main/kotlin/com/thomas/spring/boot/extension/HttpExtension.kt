package com.thomas.spring.boot.extension

import com.thomas.core.context.SessionContextHolder
import com.thomas.core.context.SessionContextHolder.currentLocale
import com.thomas.core.context.SessionContextHolder.currentToken
import com.thomas.core.context.SessionContextHolder.currentUnity
import com.thomas.core.extension.toStringOrEmpty
import java.util.function.Consumer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

val QUERY: HttpMethod = HttpMethod.valueOf("QUERY")

fun HttpStatusCode.asHttpStatus(): HttpStatus = HttpStatus.valueOf(value())

fun defaultInternalHeaders(
    requestHeaders: Map<String, String>,
    hasBody: Boolean
): Consumer<HttpHeaders> = defaultHeaders(
    requestHeaders + mapOf(
        UNITY_HEADER_TOKEN to (currentUnity.toStringOrEmpty()),
        AUTHORIZATION to ("$BEARER_TOKEN_PREFIX ${currentToken.toStringOrEmpty()}"),
    ),
    hasBody
)

fun defaultHeaders(
    requestHeaders: Map<String, String>,
    hasBody: Boolean
): Consumer<HttpHeaders> = { headers ->
    headers[ACCEPT] = APPLICATION_JSON_VALUE
    headers[ACCEPT_LANGUAGE] = currentLocale.toLanguageTag()
    headers[TRACE_HEADER_TOKEN] = SessionContextHolder.traceIdentifier
    if (hasBody) {
        headers[CONTENT_TYPE] = APPLICATION_JSON_VALUE
    }
    requestHeaders.forEach { (key, value) -> headers[key] = value }
}
