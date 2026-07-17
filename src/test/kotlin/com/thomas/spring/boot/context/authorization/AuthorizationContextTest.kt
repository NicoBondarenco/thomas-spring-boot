package com.thomas.spring.boot.context.authorization

import com.thomas.spring.boot.context.SpringBootBaseTest
import com.thomas.spring.boot.context.model.response.SimpleResponse
import com.thomas.spring.boot.exception.ClientRequestException
import com.thomas.spring.boot.extension.PROBLEM_SCOPE_PROPERTY
import com.thomas.spring.boot.extension.PROBLEM_SPECIFICS_PROPERTY
import com.thomas.spring.boot.extension.PROBLEM_TIMESTAMP_PROPERTY
import com.thomas.spring.boot.extension.PROBLEM_TRACE_PROPERTY
import com.thomas.spring.boot.extension.withDefaultHeaders
import com.thomas.spring.boot.extension.withBody
import java.net.URI
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.client.RestClient
import tools.jackson.databind.json.JsonMapper
import com.thomas.spring.boot.extension.awaitCall
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import tools.jackson.module.kotlin.readValue

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class AuthorizationContextTest : SpringBootBaseTest() {

    @Autowired
    lateinit var client: RestClient

    @Autowired
    lateinit var mapper: JsonMapper

    lateinit var baseUrl: String

    @BeforeEach
    fun setup() {
        baseUrl = "http://localhost:$port"
    }

    @Test
    fun `When authentication header is not set should return unauthorized`() = runTest(StandardTestDispatcher()) {
        val exception = assertThrows<ClientRequestException> { client.get().uri("$baseUrl/auth-test").withDefaultHeaders().awaitCall<ProblemDetail>() }
        assertEquals(HttpStatus.UNAUTHORIZED, exception.status)
        val body = mapper.readValue<ProblemDetail>(exception.body!!)
        assertNotNull(body)
        assertEquals(URI.create("/errors/general/unauthorized-action"), body.type)
        assertEquals(HttpStatus.UNAUTHORIZED.value(), body.status)
        assertEquals(URI.create("/auth-test"), body.instance)
        assertTrue(body.title!!.trim().isNotEmpty())
        assertTrue(body.detail!!.trim().isNotEmpty())
        assertNotNull(body.properties)
        assertTrue(body.properties!!.containsKey(PROBLEM_TIMESTAMP_PROPERTY))
        assertTrue(body.properties!!.containsKey(PROBLEM_SPECIFICS_PROPERTY))
        assertTrue(body.properties!!.containsKey(PROBLEM_SCOPE_PROPERTY))
        assertTrue(body.properties!!.containsKey(PROBLEM_TRACE_PROPERTY))
    }

}
