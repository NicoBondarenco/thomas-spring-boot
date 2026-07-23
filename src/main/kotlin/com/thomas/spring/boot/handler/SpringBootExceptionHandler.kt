package com.thomas.spring.boot.handler

import com.thomas.core.extension.EMPTY_STRING
import com.thomas.logger.log.KotlinLogger
import com.thomas.spring.boot.extension.httpStatus
import com.thomas.spring.boot.extension.toProblemDetail
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestControllerAdvice
class SpringBootExceptionHandler : ResponseEntityExceptionHandler(), KotlinLogger by KotlinLogger.logger(SpringBootExceptionHandler::class) {

    @ExceptionHandler(Exception::class)
    fun handleUncaughtException(
        ex: Exception,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> = handleExceptionInternal(ex, null, null, ex.httpStatus(INTERNAL_SERVER_ERROR), exchange)

    override fun createProblemDetail(
        ex: Exception,
        status: HttpStatusCode,
        defaultDetail: String,
        detailMessageCode: String?,
        detailMessageArguments: Array<out Any>?,
        exchange: ServerWebExchange
    ): ProblemDetail = ex.toProblemDetail(exchange.request.uri, status)

    override fun handleExceptionInternal(
        ex: java.lang.Exception,
        body: Any?,
        headers: HttpHeaders?,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        val response = body ?: createProblemDetail(ex, status, EMPTY_STRING, null, null, exchange)
        error(ex) { ex.message ?: EMPTY_STRING }
        return super.handleExceptionInternal(ex, response, headers, status, exchange)
    }

}
