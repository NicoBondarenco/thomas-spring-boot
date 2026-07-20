package com.thomas.spring.boot.extension

import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.util.StringUtils.randomString
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.util.LinkedMultiValueMap

class RequestExtensionTest {

    @Test
    fun `Bearer token should return null if authorization header does not exists`() {
        val request = requestMock(authToken = null)
        assertNull(request.bearerToken())
    }

    @Test
    fun `Bearer token should return null if authorization header is empty`() {
        val request = requestMock(authToken = EMPTY_STRING)
        assertNull(request.bearerToken())
    }

    @Test
    fun `Bearer token should return null if authorization header is blank`() {
        val request = requestMock(authToken = "    ")
        assertNull(request.bearerToken())
    }

    @Test
    fun `Bearer token should return null if authorization header does not starts with Bearer`() {
        val request = requestMock(authToken = randomString())
        assertNull(request.bearerToken())
    }

    @Test
    fun `Bearer token should return the token if authorization header starts with Bearer`() {
        val token = randomString()
        val request = requestMock(authToken = "Bearer $token")
        assertEquals(token, request.bearerToken())
    }

    @Test
    fun `Current Unity should return null if unity header does not exists`() {
        val request = requestMock(currentUnity = null)
        assertNull(request.currentUnity())
    }

    @Test
    fun `Current Unity should return null if unity header is empty`() {
        val request = requestMock(currentUnity = EMPTY_STRING)
        assertNull(request.currentUnity())
    }

    @Test
    fun `Current Unity should return null if unity header is blank`() {
        val request = requestMock(currentUnity = "    ")
        assertNull(request.currentUnity())
    }

    @Test
    fun `Current Unity should return null if unity header is not received`() {
        val request = requestMock(currentUnity = randomString())
        assertNull(request.currentUnity())
    }

    @Test
    fun `Current Unity should return the token if unity header is received`() {
        val token = randomUUIDv7()
        val request = requestMock(currentUnity = token.toString())
        assertEquals(token, request.currentUnity())
    }

    @Test
    fun `Trace identifier should return null if trace header is not received`() {
        val request = requestMock()
        assertNull(request.traceIdentifier())
    }

    @Test
    fun `Trace identifier should return the identifier if trace header is received`() {
        val identifier = randomString()
        val request = requestMock(traceIdentifier = identifier)
        assertEquals(identifier, request.traceIdentifier())
    }

    private fun requestMock(
        authToken: String? = null,
        currentUnity: String? = null,
        traceIdentifier: String? = null,
    ): ServerHttpRequest = mockk<ServerHttpRequest> {
        val requestHeaders = HttpHeaders()
        requestHeaders[AUTHORIZATION] = authToken
        requestHeaders["Current-Unity"] = currentUnity
        requestHeaders["Trace-Identifier"] = traceIdentifier

        every { headers } returns requestHeaders
    }

}
