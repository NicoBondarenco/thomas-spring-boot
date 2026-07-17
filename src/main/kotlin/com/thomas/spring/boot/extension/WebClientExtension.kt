package com.thomas.spring.boot.extension

import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec
import org.springframework.web.reactive.function.client.awaitEntity

suspend inline fun <reified T : Any> RequestBodySpec.awaitCall() = retrieve().awaitEntity<T>()

fun RequestBodySpec.withDefaultInternalHeaders(
    requestHeaders: Map<String, String> = emptyMap(),
    hasBody: Boolean = false
): RequestBodySpec = headers(defaultInternalHeaders(requestHeaders, hasBody))

fun RequestBodySpec.withDefaultHeaders(
    requestHeaders: Map<String, String> = emptyMap(),
    hasBody: Boolean = false
): RequestBodySpec = headers(defaultHeaders(requestHeaders, hasBody))

fun RequestBodySpec.withBody(
    body: Any?
): RequestHeadersSpec<*> = body?.let { this.bodyValue(it) } ?: this


