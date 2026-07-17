package com.thomas.spring.boot.context.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth-test")
class AuthorizationTestController {

    @GetMapping
    suspend fun test(): ResponseEntity<Map<String, Any>> = ResponseEntity.ok(
        mapOf("message" to "test")
    )

}
