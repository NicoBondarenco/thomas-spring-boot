package com.thomas.spring.boot.extension

import com.thomas.core.exception.ApplicationException
import com.thomas.core.exception.ErrorType
import com.thomas.core.exception.ErrorType.APPLICATION_ERROR
import com.thomas.core.exception.ErrorType.INVALID_ENTITY
import com.thomas.core.exception.ErrorType.INVALID_PARAMETER
import com.thomas.core.exception.ErrorType.NOT_FOUND
import com.thomas.core.exception.ErrorType.UNAUTHENTICATED_USER
import com.thomas.core.exception.ErrorType.UNAUTHORIZED_ACTION
import com.thomas.core.exception.ErrorType.UNRESOLVED_PARAMETER
import com.thomas.spring.boot.i18n.SpringMessageI18N.errorExceptionMappingExceptionResponseDefaultMessage
import com.thomas.spring.boot.model.response.ExceptionResponse
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT
import org.springframework.http.HttpStatus.PRECONDITION_FAILED

internal fun Throwable.toExceptionResponse(
    uri: String
) = this.httpStatus().let {
    ExceptionResponse(
        timestamp = ZonedDateTime.now(UTC),
        status = it,
        code = it.value(),
        path = uri,
        message = this.message ?: errorExceptionMappingExceptionResponseDefaultMessage(),
        detail = this.details()
    )
}

private fun Throwable.httpStatus() = when (this) {
    is ApplicationException -> this.type.toHttpStatus()
    else -> INTERNAL_SERVER_ERROR
}

private fun Throwable.details() = when (this) {
    is ApplicationException -> this.detail
    else -> null
}

internal fun ErrorType.toHttpStatus() = when (this) {
    UNAUTHENTICATED_USER -> UNAUTHORIZED
    UNAUTHORIZED_ACTION -> UNAUTHORIZED
    INVALID_ENTITY -> UNPROCESSABLE_CONTENT
    INVALID_PARAMETER -> BAD_REQUEST
    NOT_FOUND -> HttpStatus.NOT_FOUND
    APPLICATION_ERROR -> INTERNAL_SERVER_ERROR
    UNRESOLVED_PARAMETER -> PRECONDITION_FAILED
}
