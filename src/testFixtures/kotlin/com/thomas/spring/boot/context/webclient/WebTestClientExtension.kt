package com.thomas.spring.boot.context.webclient

import com.thomas.spring.boot.extension.QUERY
import com.thomas.spring.boot.extension.defaultHeaders
import com.thomas.spring.boot.extension.defaultInternalHeaders
import java.util.function.Consumer
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.RequestBodySpec
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec

fun WebTestClient.query() = this.method(QUERY)

inline fun <reified T : Any> RequestHeadersSpec<*>.awaitCall() = this.exchange().expectBody(T::class.java).returnResult()

fun RequestHeadersSpec<*>.withDefaultInternal(
    headers: Map<String, String> = emptyMap(),
    body: Any? = null
): RequestHeadersSpec<*> = withHeadersAndBody(defaultInternalHeaders(headers, body != null), body)

fun RequestHeadersSpec<*>.withDefault(
    headers: Map<String, String> = emptyMap(),
    body: Any? = null
): RequestHeadersSpec<*> = withHeadersAndBody(defaultHeaders(headers, body != null), body)

fun RequestHeadersSpec<*>.withHeadersAndBody(
    headers: Consumer<HttpHeaders>,
    body: Any?
): RequestHeadersSpec<*> = (headers(headers) as RequestBodySpec).let { request ->
    body?.let { request.bodyValue(it) } ?: request
}

