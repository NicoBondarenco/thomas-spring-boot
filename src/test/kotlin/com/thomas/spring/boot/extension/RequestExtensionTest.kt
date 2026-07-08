package com.thomas.spring.boot.extension

import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.util.StringUtils.randomString
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION

class RequestExtensionTest {

    @Test
    fun `Bearer token should return null if authorization header does not exists`() {
        val request = requestMock(authToken = null)
        assertNull(request.bearerToken())
    }

    @Test
    fun `Bearer token should return null if authorization header is empty`() {
        val request = requestMock(authToken = "")
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
        val request = requestMock(currentUnity = "")
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
    fun `Link identifier should return null if link header is not received`() {
        val request = requestMock()
        assertNull(request.linkIdentifier())
    }

    @Test
    fun `Link identifier should return the identifier if link header is received`() {
        val identifier = randomString()
        val request = requestMock(linkIdentifier = identifier)
        assertEquals(identifier, request.linkIdentifier())
    }

    private fun requestMock(
        authToken: String? = null,
        currentUnity: String? = null,
        linkIdentifier: String? = null,
    ): HttpServletRequest = mockk<HttpServletRequest> {
        every { getHeader(AUTHORIZATION) } returns authToken
        every { getHeader("Current-Unity") } returns currentUnity
        every { getHeader("Link-Identifier") } returns linkIdentifier
    }

}
