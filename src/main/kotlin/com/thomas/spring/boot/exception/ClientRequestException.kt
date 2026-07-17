package com.thomas.spring.boot.exception

import com.thomas.core.exception.ApplicationException
import com.thomas.core.exception.ErrorType
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class ClientRequestException(
    val status: HttpStatus,
    val method: HttpMethod,
    val url: String,
    val headers: HttpHeaders,
    val body: String?,
) : ApplicationException(
    message = "",
    type = CLIENT_REQUEST_ERROR_TYPE,
) {

    companion object {
        val CLIENT_REQUEST_ERROR_TYPE = ErrorType(
            name = "CLIENT_REQUEST_ERROR_TYPE",
            type = "client-request-error-type",
            category = "network",
            title = "Erro na requisição",
        )
    }

}
