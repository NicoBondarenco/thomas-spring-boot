package com.thomas.spring.boot.exception

import com.thomas.core.exception.ApplicationException
import com.thomas.core.exception.ErrorType
import com.thomas.spring.boot.i18n.SpringMessageI18N.errorClientRequestRequestErrorDefaultMessage
import com.thomas.spring.boot.i18n.SpringMessageI18N.errorClientRequestRequestErrorDefaultTitle
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class ClientRequestException(
    val status: HttpStatus,
    val method: HttpMethod,
    val url: String,
    val headers: HttpHeaders,
    val body: String?,
    message: String = errorClientRequestRequestErrorDefaultMessage(
        buildRequestLine(status, method, url, headers, body)
    ),
    type: ErrorType = requestDefaultErrorType()
) : ApplicationException(
    message = message,
    type = type,
) {

    companion object {

        private fun buildRequestLine(
            status: HttpStatus,
            method: HttpMethod,
            url: String,
            headers: HttpHeaders,
            body: String?,
        ): String = buildString {
            append(status.value())
            append(" (${status.name})")
            append(" ${method.name()}")
            append(" $url")
            append(" - HEADERS [")
            headers.forEach { name, value ->
                append(" $name: ${value.joinToString(", ")}")
            }
            append("] - BODY [$body]")
        }

        fun requestDefaultErrorType() = ErrorType(
            name = "CLIENT_REQUEST_ERROR_TYPE",
            type = "client-request-error-type",
            category = "network",
            title = errorClientRequestRequestErrorDefaultTitle(),
        )
    }

}
