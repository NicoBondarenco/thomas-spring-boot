package com.thomas.spring.boot.context.controller

import com.thomas.spring.boot.context.model.response.SimpleResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth-test")
class AuthorizationTestController {

    var simpleResponse: SimpleResponse = SimpleResponse()

    @GetMapping
    suspend fun test(): ResponseEntity<SimpleResponse> = ResponseEntity.ok(simpleResponse)

}
