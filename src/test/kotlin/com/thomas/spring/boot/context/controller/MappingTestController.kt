package com.thomas.spring.boot.context.controller

import com.thomas.spring.boot.context.model.request.QueryRequest
import com.thomas.spring.boot.context.model.response.SimpleResponse
import com.thomas.spring.boot.mapping.QueryMapping
import java.net.URI
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/mapping-test")
class MappingTestController {

    @GetMapping("/{id}")
    suspend fun get(
        @PathVariable id: UUID
    ): ResponseEntity<SimpleResponse> = ResponseEntity.ok(SimpleResponse(id))

    @QueryMapping
    suspend fun query(
        @RequestBody body: QueryRequest
    ): ResponseEntity<SimpleResponse> = ResponseEntity.ok(SimpleResponse(body.idSimple, body.nameSimple, body.valueSimple))

    @QueryMapping(["/empty"])
    suspend fun empty(): ResponseEntity<SimpleResponse> = ResponseEntity.ok(SimpleResponse())

    @QueryMapping(["/query"])
    suspend fun path(
        @RequestBody body: QueryRequest
    ): ResponseEntity<SimpleResponse> = ResponseEntity.ok(SimpleResponse(body.idSimple, body.nameSimple, body.valueSimple))

    @PostMapping
    suspend fun post(
        @RequestBody body: SimpleResponse
    ): ResponseEntity<SimpleResponse> = ResponseEntity.created(URI.create("/get/${body.idSimple}")).body(body)

    @PutMapping("/{id}")
    suspend fun put(
        @PathVariable id: UUID,
        @RequestBody body: SimpleResponse
    ): ResponseEntity<SimpleResponse> = ResponseEntity.ok(body.copy(idSimple = id))

    @PatchMapping("/{id}")
    suspend fun patch(
        @PathVariable id: UUID,
        @RequestBody body: SimpleResponse
    ): ResponseEntity<SimpleResponse> = ResponseEntity.ok(body.copy(idSimple = id))

    @DeleteMapping("/{id}")
    suspend fun delete(
        @PathVariable id: UUID
    ): ResponseEntity<Any> = ResponseEntity.noContent().build()

}
