package com.thomas.spring.boot.handler

import com.thomas.spring.boot.exception.ClientRequestException
import com.thomas.spring.boot.extension.asHttpStatus
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

val HttpStatusCode.isStatusError: Boolean
    get() = this.is4xxClientError || this.is5xxServerError

fun ClientResponse.errorHandler(): Mono<ClientRequestException> = this.bodyToMono<String>().flatMap { errorBody ->
    Mono.error<ClientRequestException>(this.toClientRequestException(errorBody))
}.switchIfEmpty(
    Mono.error(this.toClientRequestException(null))
)

fun ClientResponse.httpStatus(): HttpStatus = this.statusCode().asHttpStatus()

fun ClientResponse.toClientRequestException(
    body: String?
): ClientRequestException = ClientRequestException(
    method = this.request().method,
    status = this.httpStatus(),
    url = this.request().uri.toString(),
    headers = this.headers().asHttpHeaders(),
    body = body,
)

