package com.thomas.spring.boot.context.authorization

import com.thomas.core.context.SessionContextHolder.currentToken
import com.thomas.core.context.SessionContextHolder.currentUser
import com.thomas.core.generator.SecurityUserGenerator.generateSecurityUser
import com.thomas.spring.boot.context.SpringBootBaseTest
import com.thomas.spring.boot.context.controller.AuthorizationTestController
import com.thomas.spring.boot.context.model.response.SimpleResponse
import com.thomas.spring.boot.exception.ClientRequestException
import com.thomas.spring.boot.extension.PROBLEM_SCOPE_PROPERTY
import com.thomas.spring.boot.extension.PROBLEM_SPECIFICS_PROPERTY
import com.thomas.spring.boot.extension.PROBLEM_TIMESTAMP_PROPERTY
import com.thomas.spring.boot.extension.PROBLEM_TRACE_PROPERTY
import com.thomas.spring.boot.extension.awaitCall
import com.thomas.spring.boot.extension.withDefault
import com.thomas.spring.boot.extension.withDefaultInternal
import com.thomas.spring.boot.token.TokenDecrypter
import java.net.URI
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ProblemDetail
import org.springframework.web.reactive.function.client.WebClient
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class AuthorizationContextTest : SpringBootBaseTest() {

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
        baseUrl = "http://localhost:$port"
    }

    @Test
    fun `When authentication header is not set should return unauthorized`() = runTest(StandardTestDispatcher()) {
        val exception = assertThrows<ClientRequestException> {
            client.get().uri("$baseUrl/auth-test").withDefault().awaitCall<ProblemDetail>()
        }
        assertEquals(UNAUTHORIZED, exception.status)
        val body = mapper.readValue<ProblemDetail>(exception.body!!)
        assertNotNull(body)
        assertEquals(URI.create("/errors/general/unauthorized-action"), body.type)
        assertEquals(UNAUTHORIZED.value(), body.status)
        assertEquals(URI.create("http://localhost:$port/auth-test"), body.instance)
        assertTrue(body.title!!.trim().isNotEmpty())
        assertTrue(body.detail!!.trim().isNotEmpty())
        assertNotNull(body.properties)
        assertTrue(body.properties!!.containsKey(PROBLEM_TIMESTAMP_PROPERTY))
        assertTrue(body.properties!!.containsKey(PROBLEM_SPECIFICS_PROPERTY))
        assertTrue(body.properties!!.containsKey(PROBLEM_SCOPE_PROPERTY))
        assertTrue(body.properties!!.containsKey(PROBLEM_TRACE_PROPERTY))
    }

    @Test
    fun `When authentication header is set correctly should return the response`() = runTest(StandardTestDispatcher()) {
        val securityUser = generateSecurityUser()
        currentUser = securityUser
        currentToken = decrypter.encrypt(securityUser)
        val expected = SimpleResponse()
        controller.simpleResponse = expected
        val response = client.get().uri("$baseUrl/auth-test").withDefaultInternal().awaitCall<SimpleResponse>()
        assertEquals(OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(expected, response.body)
    }

}
