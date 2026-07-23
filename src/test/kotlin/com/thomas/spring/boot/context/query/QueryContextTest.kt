package com.thomas.spring.boot.context.query

import com.thomas.core.extension.randomUUIDv7
import com.thomas.spring.boot.context.SpringBootBaseTest
import com.thomas.spring.boot.context.model.request.QueryRequest
import com.thomas.spring.boot.context.model.response.SimpleResponse
import com.thomas.spring.boot.extension.awaitCall
import com.thomas.spring.boot.extension.query
import com.thomas.spring.boot.extension.withDefault
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class QueryContextTest : SpringBootBaseTest() {

    @Autowired
    lateinit var client: WebClient

    lateinit var baseUrl: String

    @BeforeEach
    fun setup() {
        baseUrl = "http://localhost:$port/public/mapping-test"
    }

    @Test
    fun `When method is GET should return correctly`() = runTest(StandardTestDispatcher()) {
        val expected = randomUUIDv7()
        val response = client.get().uri("$baseUrl/$expected").withDefault().awaitCall<SimpleResponse>()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body!!.idSimple)
    }

    @Test
    fun `When method is QUERY should return correctly`() = runTest(StandardTestDispatcher()) {
        val body = QueryRequest()
        val expected = SimpleResponse(body.idSimple, body.nameSimple, body.valueSimple)
        val response = client.query().uri(baseUrl).withDefault(body = body).awaitCall<SimpleResponse>()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
    }

    @Test
    fun `When method is QUERY with path should return correctly`() = runTest(StandardTestDispatcher()) {
        val body = QueryRequest()
        val expected = SimpleResponse(body.idSimple, body.nameSimple, body.valueSimple)
        val response = client.query().uri("$baseUrl/query").withDefault(body = expected).awaitCall<SimpleResponse>()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
    }

    @Test
    fun `When method is QUERY without body should return correctly`() = runTest(StandardTestDispatcher()) {
        val response = client.query().uri("$baseUrl/empty").awaitCall<SimpleResponse>()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun `When method is POST should return correctly`() = runTest(StandardTestDispatcher()) {
        val expected = SimpleResponse()
        val response = client.post().uri(baseUrl).withDefault(body = expected).awaitCall<SimpleResponse>()
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(expected, response.body)
    }

    @Test
    fun `When method is PUT should return correctly`() = runTest(StandardTestDispatcher()) {
        val expected = SimpleResponse()
        val body = expected.copy(idSimple = randomUUIDv7())
        val response = client.put().uri("$baseUrl/${expected.idSimple}").withDefault(body = body).awaitCall<SimpleResponse>()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
    }

    @Test
    fun `When method is PATCH should return correctly`() = runTest(StandardTestDispatcher()) {
        val expected = SimpleResponse()
        val body = expected.copy(idSimple = randomUUIDv7())
        val response = client.patch().uri("$baseUrl/${expected.idSimple}").withDefault(body = body).awaitCall<SimpleResponse>()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body)
    }

    @Test
    fun `When method is DELETE should return correctly`() = runTest(StandardTestDispatcher()) {
        val response = client.delete().uri("$baseUrl/${randomUUIDv7()}").withDefault().awaitCall<Any>()
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

}
