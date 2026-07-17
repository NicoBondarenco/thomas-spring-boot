package com.thomas.spring.boot.mapping

import com.thomas.spring.boot.extension.EMPTY_STRING
import org.springframework.core.annotation.AliasFor
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@RequestMapping(
    method = [GET],
    produces = [APPLICATION_JSON_VALUE],
)
annotation class GetMappingJson(
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
