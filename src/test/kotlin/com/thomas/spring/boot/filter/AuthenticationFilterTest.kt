package com.thomas.spring.boot.filter

import ch.qos.logback.classic.Level.TRACE
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.thomas.core.context.SessionContextHolder
import com.thomas.core.context.SessionContextHolder.context
import com.thomas.core.context.SessionContextHolder.currentUser
import com.thomas.core.context.SessionContextHolder.updateContext
import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.generator.SecurityUserGenerator.generateSecurityUser
import com.thomas.core.util.StringUtils.randomString
import com.thomas.spring.boot.extension.clearRequestContext
import com.thomas.spring.boot.security.SecurityUserAuthentication
import com.thomas.spring.boot.token.TokenDecrypter
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.SecurityContextHolder.getContext
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class AuthenticationFilterTest {

    private lateinit var classLogger: Logger
    private lateinit var loggerAppender: ListAppender<ILoggingEvent>

    private val decrypterMock = mockk<TokenDecrypter>()
    private val exchangeMock = mockk<ServerWebExchange>()
    private val requestMock = mockk<ServerHttpRequest>()
    private val chainMock = mockk<WebFilterChain>()
    private val requestHeaders = HttpHeaders()

    private lateinit var filter: AuthenticationFilter

    @BeforeEach
    fun setup() {
        SessionContextHolder.clearRequestContext()
        clearAllMocks()
        filter = AuthenticationFilter(decrypterMock)
        setupLogger(AuthenticationFilter::class.java.name)
        setupRequest()
    }

    private fun setupLogger(name: String) {
        classLogger = LoggerFactory.getLogger(name) as Logger
        classLogger.level = TRACE
        loggerAppender = ListAppender<ILoggingEvent>()
        loggerAppender.context = LoggerFactory.getILoggerFactory() as LoggerContext
        loggerAppender.start()
        classLogger.addAppender(loggerAppender)
    }

    private fun setupRequest() {
        every { exchangeMock.request } returns requestMock
        every { requestMock.headers } returns requestHeaders
        every { chainMock.filter(any()) } returns Mono.empty()
    }

    @Test
    fun `Authentication filter should process token correctly`() {
        requestHeaders[AUTHORIZATION] = "Bearer ${randomString()}"
        every { decrypterMock.decrypt(any()) } returns generateSecurityUser()
        filter.filter(exchangeMock, chainMock)
        assertEquals(currentUser, getContext().authentication?.details)
    }

    @Test
    fun `Authentication filter should clear security context on error`() {
        updateContext { it.copy(user = generateSecurityUser(), token = randomString(), currentOrganization = randomUUIDv7()) }
        getContext().authentication = SecurityUserAuthentication(currentUser, randomString(), true)

        requestHeaders[AUTHORIZATION] = "Bearer ${randomString()}"
        every { decrypterMock.decrypt(any()) } throws RuntimeException("Error on decrypt")
        assertThrows<Exception> { filter.filter(exchangeMock, chainMock) }

        assertNull(context.currentUser)
        assertNull(context.currentToken)
        assertNull(context.currentOrganization)
    }

    @Test
    fun `Authentication filter should log default message when exception has no message`() {
        updateContext { it.copy(user = generateSecurityUser(), token = randomString(), currentOrganization = randomUUIDv7()) }
        getContext().authentication = SecurityUserAuthentication(currentUser, randomString(), true)

        requestHeaders[AUTHORIZATION] = "Bearer ${randomString()}"
        every { decrypterMock.decrypt(any()) } throws RuntimeException()
        assertThrows<Exception> { filter.filter(exchangeMock, chainMock) }

        assertNull(context.currentUser)
        assertNull(context.currentToken)
        assertNull(context.currentOrganization)
    }

    @Test
    fun `Authentication filter should clear security context when token does not exists`() {
        updateContext { it.copy(user = generateSecurityUser(), token = randomString(), currentOrganization = randomUUIDv7()) }
        getContext().authentication = SecurityUserAuthentication(currentUser, randomString(), true)

        filter.filter(exchangeMock, chainMock)

        assertNull(context.currentUser)
        assertNull(context.currentToken)
        assertNull(context.currentOrganization)
    }

}
