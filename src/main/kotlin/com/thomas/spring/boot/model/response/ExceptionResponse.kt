package com.thomas.spring.boot.model.response

import java.time.ZonedDateTime
import org.springframework.http.HttpStatus

data class ExceptionResponse(
    val timestamp: ZonedDateTime,
    val status: HttpStatus,
    val code: Int,
    val path: String,
    val message: String,
    val detail: Map<String, List<String>>?
)
