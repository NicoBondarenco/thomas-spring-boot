package com.thomas.spring.boot.context.controller

import com.thomas.core.context.SessionContextHolder.currentLocale
import com.thomas.core.context.SessionContextHolder.currentToken
import com.thomas.core.context.SessionContextHolder.currentUnity
import com.thomas.core.context.SessionContextHolder.currentUserId
import com.thomas.core.context.SessionContextHolder.traceIdentifier
import com.thomas.core.extension.EMPTY_STRING
import com.thomas.core.util.NumberUtils.randomInteger
import com.thomas.spring.boot.context.model.response.ContextResponse
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/context-test")
class ContextTestController {

    @GetMapping
    suspend fun test(
        @RequestHeader("Call-Number") call: String
    ): ResponseEntity<ContextResponse> {
        if (call.toInt() % 2 == 0) {
            delay(randomInteger(20, 1000).milliseconds)
        } else {
            delay(randomInteger(1500, 3000).milliseconds)
        }
        return ResponseEntity.ok(
            ContextResponse(
                currentUser = currentUserId,
                currentToken = currentToken ?: EMPTY_STRING,
                currentLocale = currentLocale.toLanguageTag(),
                currentUnity = currentUnity,
                withUser = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication!!.name,
                traceId = traceIdentifier,
                callNumber = call,
            )
        )
    }

}
