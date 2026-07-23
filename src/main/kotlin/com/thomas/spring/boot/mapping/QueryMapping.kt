package com.thomas.spring.boot.mapping

import com.thomas.core.extension.EMPTY_STRING
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION
import org.springframework.core.annotation.AliasFor
import org.springframework.web.bind.annotation.RequestMapping


@Target(FUNCTION)
@Retention(RUNTIME)
@MustBeDocumented
@RequestMapping
annotation class QueryMapping(
    @get:AliasFor(annotation = RequestMapping::class)
    val value: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val path: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val name: String = EMPTY_STRING,
    @get:AliasFor(annotation = RequestMapping::class)
    val params: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val headers: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val consumes: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val produces: Array<String> = [],
)
