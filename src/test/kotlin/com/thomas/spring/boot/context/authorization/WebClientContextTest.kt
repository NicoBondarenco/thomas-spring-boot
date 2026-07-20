package com.thomas.spring.boot.context.authorization

import com.thomas.core.context.SessionContextHolder.currentToken
import com.thomas.core.context.SessionContextHolder.currentUnity
import com.thomas.core.context.SessionContextHolder.currentUser
import com.thomas.core.context.SessionContextHolder.traceIdentifier
import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.generator.SecurityUserGenerator.generateSecurityUser
import com.thomas.core.util.NumberUtils.randomBigDecimal
import com.thomas.spring.boot.context.SpringBootBaseTest
import com.thomas.spring.boot.context.controller.ClientTestController
import com.thomas.spring.boot.context.model.response.InternalHeadersResponse
import com.thomas.spring.boot.context.model.response.SimpleResponse
import com.thomas.spring.boot.context.model.response.TypedResponse
import com.thomas.spring.boot.extension.awaitCall
import com.thomas.spring.boot.extension.withDefaultHeaders
import com.thomas.spring.boot.extension.withDefaultInternalHeaders
import com.thomas.spring.boot.token.TokenDecrypter
import java.math.BigDecimal
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import tools.jackson.databind.json.JsonMapper

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class WebClientContextTest : SpringBootBaseTest() {

    @Autowired
    lateinit var client: WebClient

    @Autowired
    lateinit var mapper: JsonMapper

    @Autowired
    lateinit var controller: ClientTestController

    @Autowired
    lateinit var decrypter: TokenDecrypter

    lateinit var baseUrl: String

    @BeforeEach
    fun setup() {
        baseUrl = "http://localhost:$port/public/client-test"
    }

    @Test
    fun `When response is simple should map response correctly`() = runTest(StandardTestDispatcher()) {
        val expected = SimpleResponse()
        controller.simpleResponse = expected
        testCall<SimpleResponse>(baseUrl, expected)
    }

    @Test
    fun `When response is simple list should map response correctly`() = runTest(StandardTestDispatcher()) {
        val expected = listOf(SimpleResponse())
        controller.simpleList = expected
        testCall<List<SimpleResponse>>("$baseUrl/list", expected)
    }

    @Test
    fun `When response is typed should map response correctly`() = runTest(StandardTestDispatcher()) {
        val expected = TypedResponse(valueTyped = randomBigDecimal())
        controller.typedResponse = expected
        testCall<TypedResponse<BigDecimal>>("$baseUrl/typed", expected)
    }

    @Test
    fun `When response is typed list should map response correctly`() = runTest(StandardTestDispatcher()) {
        val expected = listOf(TypedResponse(valueTyped = randomBigDecimal()))
        controller.typedList = expected
        testCall<List<TypedResponse<BigDecimal>>>("$baseUrl/typed-list", expected)
    }

    @Test
    fun `When response is complex should map response correctly`() = runTest(StandardTestDispatcher()) {
        val expected = TypedResponse(valueTyped = SimpleResponse())
        controller.complexTyped = expected
        testCall<TypedResponse<SimpleResponse>>("$baseUrl/typed-complex", expected)
    }

    @Test
    fun `When response is complex list should map response correctly`() = runTest(StandardTestDispatcher()) {
        val expected = listOf(TypedResponse(valueTyped = SimpleResponse()))
        controller.complexList = expected
        testCall<List<TypedResponse<SimpleResponse>>>("$baseUrl/typed-complex-list", expected)
    }

    @Test
    fun `When internal headers are set should receive them correctly`() = runTest(StandardTestDispatcher()) {
        currentUser = generateSecurityUser()
        currentUnity = randomUUIDv7()
        currentToken = decrypter.encrypt(currentUser)
        val expected = InternalHeadersResponse(
            unityId = currentUnity!!,
            traceId = traceIdentifier,
            authHeader = "Bearer $currentToken",
        )
        val response = client.get().uri("$baseUrl/internal-headers").withDefaultInternalHeaders().awaitCall<InternalHeadersResponse>()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
    }

    private suspend inline fun <reified T : Any> testCall(uri: String, expected: T) {
        val response = client.get().uri(uri).withDefaultHeaders().awaitCall<T>()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
    }

}
