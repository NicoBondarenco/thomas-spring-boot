package com.thomas.spring.boot.extension

import com.thomas.core.util.StringUtils.randomString
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION

class RequestExtensionTest {

    @Test
    fun `Bearer token should return null if authorization headers does not exists`() {
        val request = requestMock(null)
        assertNull(request.bearerToken())
    }

    @Test
    fun `Bearer token should return null if authorization headers is empty`() {
        val request = requestMock("")
        assertNull(request.bearerToken())
    }

    @Test
    fun `Bearer token should return null if authorization headers is blank`() {
        val request = requestMock("    ")
        assertNull(request.bearerToken())
    }

    @Test
    fun `Bearer token should return null if authorization headers does not starts with Bearer`() {
        val request = requestMock(randomString())
        assertNull(request.bearerToken())
    }

    @Test
    fun `Bearer token should return the token if authorization headers starts with Bearer`() {
        val token = randomString()
        val request = requestMock("Bearer $token")
        assertEquals(token, request.bearerToken())
    }

    private fun requestMock(authToken: String?): HttpServletRequest = mockk<HttpServletRequest> {
        every { getHeader(AUTHORIZATION) } returns authToken
    }

}
