package com.thomas.spring.boot.mapping

import org.springframework.core.annotation.AliasFor
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.PATCH


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@RequestMapping(
    method = [PATCH],
    produces = [APPLICATION_JSON_VALUE],
    consumes = [APPLICATION_JSON_VALUE],
)
annotation class PatchMappingJson(
    @get:AliasFor(annotation = RequestMapping::class)
    val value: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val name: String = "",
    @get:AliasFor(annotation = RequestMapping::class)
    val path: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val params: Array<String> = [],
    @get:AliasFor(annotation = RequestMapping::class)
    val headers: Array<String> = [],
)
