package com.thomas.spring.boot.handler

import com.thomas.spring.boot.exception.ClientRequestException
import com.thomas.spring.boot.extension.asHttpStatus
import java.net.URI
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler

class RestClientErrorHandler : ResponseErrorHandler {

    override fun hasError(
        response: ClientHttpResponse
    ): Boolean = response.statusCode.is4xxClientError || response.statusCode.is5xxServerError

    override fun handleError(
        url: URI,
        method: HttpMethod,
        response: ClientHttpResponse
    ) {
        val body: String? = response.body.bufferedReader().use { it.readText() }.takeIf { it.trim().isNotEmpty() }
        throw ClientRequestException(
            status = response.statusCode.asHttpStatus(),
            method = method,
            url = url.toString(),
            headers = response.headers,
            body = body,
        )
    }
}
