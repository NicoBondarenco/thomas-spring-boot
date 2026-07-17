package com.thomas.spring.boot.context.controller

import com.thomas.core.extension.toUUIDOrNull
import com.thomas.core.util.NumberUtils.randomBigDecimal
import com.thomas.spring.boot.context.model.request.SimpleRequest
import com.thomas.spring.boot.context.model.response.InternalHeadersResponse
import com.thomas.spring.boot.context.model.response.SimpleResponse
import com.thomas.spring.boot.context.model.response.TypedResponse
import com.thomas.spring.boot.extension.TRACE_HEADER_TOKEN
import com.thomas.spring.boot.extension.UNITY_HEADER_TOKEN
import java.math.BigDecimal
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/client-test")
class ClientTestController {

    var simpleResponse: SimpleResponse = SimpleResponse()
    var simpleList: List<SimpleResponse> = listOf(SimpleResponse(), SimpleResponse())
    var typedResponse: TypedResponse<BigDecimal> = TypedResponse(valueTyped = randomBigDecimal())
    var typedList: List<TypedResponse<BigDecimal>> =
        listOf(TypedResponse(valueTyped = randomBigDecimal()), TypedResponse(valueTyped = randomBigDecimal()))
    var complexTyped: TypedResponse<SimpleResponse> = TypedResponse(valueTyped = SimpleResponse())
    var complexList: List<TypedResponse<SimpleResponse>> =
        listOf(TypedResponse(valueTyped = SimpleResponse()), TypedResponse(valueTyped = SimpleResponse()))

    @GetMapping
    suspend fun test(): ResponseEntity<SimpleResponse> = ResponseEntity.ok(
        simpleResponse
    )

    @GetMapping("/list")
    suspend fun list(): ResponseEntity<List<SimpleResponse>> = ResponseEntity.ok(
        simpleList
    )

    @GetMapping("/typed")
    suspend fun typed(): ResponseEntity<TypedResponse<BigDecimal>> = ResponseEntity.ok(
        typedResponse
    )

    @GetMapping("/typed-list")
    suspend fun typedList(): ResponseEntity<List<TypedResponse<BigDecimal>>> = ResponseEntity.ok(
        typedList
    )

    @GetMapping("/typed-complex")
    suspend fun typedComplex(): ResponseEntity<TypedResponse<SimpleResponse>> = ResponseEntity.ok(
        complexTyped
    )

    @GetMapping("/typed-complex-list")
    suspend fun typedComplexList(): ResponseEntity<List<TypedResponse<SimpleResponse>>> = ResponseEntity.ok(
        complexList
    )

    @GetMapping("/internal-headers")
    suspend fun internalHeaders(
        @RequestHeader headers: HttpHeaders
    ): ResponseEntity<InternalHeadersResponse> = ResponseEntity.ok(
        InternalHeadersResponse(
            unityId = headers[AUTHORIZATION]!!.first().toUUIDOrNull()!!,
            traceId = headers[UNITY_HEADER_TOKEN]!!.first(),
            authHeader = headers[TRACE_HEADER_TOKEN]!!.first(),
        )
    )

    @PostMapping("/post-body")
    suspend fun postBody(
        @RequestBody body: SimpleRequest
    ): ResponseEntity<SimpleResponse> = ResponseEntity.ok(
        SimpleResponse(
            idSimple = body.idSimple,
            nameSimple = body.nameSimple,
            valueSimple = body.valueSimple,
        )
    )

}
