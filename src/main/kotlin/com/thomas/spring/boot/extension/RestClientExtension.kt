package com.thomas.spring.boot.extension

import com.thomas.core.extension.withCurrentSessionContextVT
import org.springframework.web.client.RestClient.RequestBodySpec
import org.springframework.web.client.RestClient.RequestHeadersSpec
import org.springframework.web.client.toEntity

suspend inline fun <reified T : Any> RequestBodySpec.awaitCall() = withCurrentSessionContextVT {
    retrieve().toEntity<T>()
}

fun RequestHeadersSpec<*>.withDefaultInternalHeaders(
    requestHeaders: Map<String, String> = emptyMap(),
    hasBody: Boolean = false
): RequestBodySpec = headers(defaultInternalHeaders(requestHeaders, hasBody)) as RequestBodySpec

fun RequestHeadersSpec<*>.withDefaultHeaders(
    requestHeaders: Map<String, String> = emptyMap(),
    hasBody: Boolean = false
): RequestBodySpec = headers(defaultHeaders(requestHeaders, hasBody)) as RequestBodySpec

fun RequestBodySpec.withBody(
    body: Any
): RequestBodySpec = this.body(body)
