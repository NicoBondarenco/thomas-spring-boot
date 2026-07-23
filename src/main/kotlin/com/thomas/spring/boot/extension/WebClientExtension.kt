package com.thomas.spring.boot.extension

import java.util.function.Consumer
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec
import org.springframework.web.reactive.function.client.bodyValueWithType
import org.springframework.web.reactive.function.client.toEntity

fun WebClient.query() = this.method(QUERY)

suspend inline fun <reified T : Any> RequestHeadersSpec<*>.awaitCall() = retrieve().toEntity<T>().awaitSingle()

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
    if (body!= null) {
        request.bodyValueWithType<Any>(body)
    } else {
        request
    }
}

