package com.thomas.spring.boot.extension

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec
import org.springframework.web.reactive.function.client.toEntity

fun WebClient.get(
    uri: String,
    variables: Array<Any> = emptyArray<Any>()
): RequestHeadersSpec<*> = get().uri(uri, *variables)

suspend inline fun <reified T : Any> RequestBodySpec.awaitCall() = retrieve().toEntity<T>().awaitSingle()

fun RequestHeadersSpec<*>.withDefaultInternalHeaders(
    requestHeaders: Map<String, String> = emptyMap(),
    hasBody: Boolean = false
): RequestBodySpec = headers(defaultInternalHeaders(requestHeaders, hasBody)) as RequestBodySpec

fun RequestHeadersSpec<*>.withDefaultHeaders(
    requestHeaders: Map<String, String> = emptyMap(),
    hasBody: Boolean = false
): RequestBodySpec = headers(defaultHeaders(requestHeaders, hasBody)) as RequestBodySpec


