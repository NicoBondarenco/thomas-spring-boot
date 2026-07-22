package com.thomas.spring.boot.mapping

import com.thomas.core.extension.EMPTY_STRING
import org.springframework.core.annotation.AliasFor
import org.springframework.web.bind.annotation.RequestMapping


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@RequestMapping
annotation class QueryMapping(
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
    @get:AliasFor(annotation = RequestMapping::class)
    val consumes: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val produces: Array<String> = [],
)
