package com.thomas.spring.boot.extension

import com.thomas.core.context.SessionContextHolder.traceIdentifier
import com.thomas.core.context.SessionContextHolder.scopeIdentifier
import com.thomas.core.exception.ApplicationException
import com.thomas.core.exception.ErrorType
import com.thomas.core.exception.ErrorType.Companion.APPLICATION_ERROR
import com.thomas.core.exception.ErrorType.Companion.INVALID_ENTITY
import com.thomas.core.exception.ErrorType.Companion.INVALID_PARAMETER
import com.thomas.core.exception.ErrorType.Companion.NOT_FOUND
import com.thomas.core.exception.ErrorType.Companion.UNAUTHENTICATED_USER
import com.thomas.core.exception.ErrorType.Companion.UNAUTHORIZED_ACTION
import com.thomas.core.exception.ErrorType.Companion.UNRESOLVED_PARAMETER
import com.thomas.spring.boot.i18n.SpringMessageI18N.errorExceptionMappingExceptionResponseDefaultMessage
import java.net.URI
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.PRECONDITION_FAILED
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail

const val ERROR_RESOURCE_URI = "/errors"
const val ERROR_DEFAULT_CATEGORY = "/server-error"
const val ERROR_DEFAULT_TYPE = "/internal-server-error"

const val ERROR_DEFAULT_RESOURCE = ERROR_RESOURCE_URI + ERROR_DEFAULT_CATEGORY + ERROR_DEFAULT_TYPE

const val PROBLEM_TIMESTAMP_PROPERTY = "timestamp"
const val PROBLEM_SPECIFICS_PROPERTY = "specifics"
const val PROBLEM_SCOPE_PROPERTY = "scope_identifier"
const val PROBLEM_TRACE_PROPERTY = "trace_identifier"

internal fun Throwable.toProblemDetail(
    uri: URI,
    status: HttpStatusCode,
) = this.httpStatus(status).let { httpStatus ->
    ProblemDetail.forStatus(httpStatus).also { problem ->
        problem.type = this.errorURI()
        problem.instance = uri
        problem.title = this.errorTitle()
        problem.detail = this.message ?: errorExceptionMappingExceptionResponseDefaultMessage()
        problem.setProperty(PROBLEM_TIMESTAMP_PROPERTY, ZonedDateTime.now(UTC))
        problem.setProperty(PROBLEM_SPECIFICS_PROPERTY, this.details())
        problem.setProperty(PROBLEM_SCOPE_PROPERTY, scopeIdentifier)
        problem.setProperty(PROBLEM_TRACE_PROPERTY, traceIdentifier)
    }
}

private fun Throwable.errorURI(): URI = when (this) {
    is ApplicationException -> this.type.errorURI()
    else -> ERROR_DEFAULT_RESOURCE
}.let { URI.create(it) }

private fun Throwable.errorTitle(): String = when (this) {
    is ApplicationException -> this.type.title
    else -> APPLICATION_ERROR.title
}

fun Throwable.httpStatus(status: HttpStatusCode): HttpStatus = when (this) {
    is ApplicationException -> this.type.toHttpStatus(status)
    else -> INTERNAL_SERVER_ERROR
}

private fun Throwable.details() = when (this) {
    is ApplicationException -> this.detail
    else -> null
}

internal fun ErrorType.toHttpStatus(status: HttpStatusCode): HttpStatus = when (this) {
    UNAUTHENTICATED_USER -> UNAUTHORIZED
    UNAUTHORIZED_ACTION -> UNAUTHORIZED
    INVALID_ENTITY -> UNPROCESSABLE_CONTENT
    INVALID_PARAMETER -> BAD_REQUEST
    NOT_FOUND -> HttpStatus.NOT_FOUND
    APPLICATION_ERROR -> INTERNAL_SERVER_ERROR
    UNRESOLVED_PARAMETER -> PRECONDITION_FAILED
    else -> HttpStatus.valueOf(status.value())
}

internal fun ErrorType.errorURI(): String = "$ERROR_RESOURCE_URI/${this.category}/${this.type}"
