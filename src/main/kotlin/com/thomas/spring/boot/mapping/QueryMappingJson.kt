package com.thomas.spring.boot.mapping

import com.thomas.spring.boot.extension.EMPTY_STRING
import org.springframework.core.annotation.AliasFor
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.RequestMapping


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@RequestMapping(
    produces = [APPLICATION_JSON_VALUE],
    consumes = [APPLICATION_JSON_VALUE],
)
annotation class QueryMappingJson(
    @get:AliasFor(annotation = RequestMapping::class)
    val value: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val name: String = EMPTY_STRING,
    @get:AliasFor(annotation = RequestMapping::class)
    val path: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val params: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val headers: Array<String> = [],
)
