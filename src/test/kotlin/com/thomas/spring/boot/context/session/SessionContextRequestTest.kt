package com.thomas.spring.boot.context.session

import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.extension.toStringOrEmpty
import com.thomas.core.generator.SecurityUserGenerator.generateSecurityUser
import com.thomas.core.util.LocaleUtils.randomLocale
import com.thomas.core.util.StringUtils.randomString
import com.thomas.spring.boot.context.SpringBootBaseTest
import com.thomas.spring.boot.context.controller.AuthorizationTestController
import com.thomas.spring.boot.context.model.response.ContextResponse
import com.thomas.spring.boot.extension.TRACE_HEADER_TOKEN
import com.thomas.spring.boot.extension.UNITY_HEADER_TOKEN
import com.thomas.spring.boot.extension.awaitCall
import com.thomas.spring.boot.extension.withDefault
import com.thomas.spring.boot.token.TokenDecrypter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.OK
import org.springframework.web.reactive.function.client.WebClient
import tools.jackson.databind.json.JsonMapper

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class SessionContextRequestTest : SpringBootBaseTest() {

    @Autowired
    private lateinit var client: WebClient

    @Autowired
    private lateinit var mapper: JsonMapper

    @Autowired
    private lateinit var controller: AuthorizationTestController

    @Autowired
    private lateinit var decrypter: TokenDecrypter

    private lateinit var baseUrl: String

    @BeforeEach
    fun setup() {
        baseUrl = "http://localhost:$port/context-test"
    }

    @Test
    fun `When authentication header is set correctly should return the response`() = runTest(StandardTestDispatcher()) {
        (1..50).map {
            requestAsync()
        }.awaitAll()
//        requestAsync().await()
    }

    private suspend fun requestAsync() = coroutineScope {
        async {
            val expected = generateContextResponse()
            val response = client.get().uri(baseUrl).withDefault(headers = expected.headers()).awaitCall<ContextResponse>()
            assertEquals(OK, response.statusCode)
            assertEquals(expected, response.body)
        }
    }

    private fun generateContextResponse(): ContextResponse = generateSecurityUser().let { user ->
        ContextResponse(
            currentUser = user.userId,
            currentToken = decrypter.encrypt(user),
            currentLocale = randomLocale().toLanguageTag(),
            currentUnity = listOf(randomUUIDv7(), null).random(),
            withUser = user.userId.toString(),
            traceId = randomString(spaces = false, numbers = false),
        )
    }

    private fun ContextResponse.headers() = mapOf(
        AUTHORIZATION to "Bearer $currentToken",
        ACCEPT_LANGUAGE to currentLocale,
        UNITY_HEADER_TOKEN to currentUnity.toStringOrEmpty(),
        TRACE_HEADER_TOKEN to traceId,
    )
}
