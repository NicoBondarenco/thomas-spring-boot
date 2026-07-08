package com.thomas.spring.boot.filter

import ch.qos.logback.classic.Level.TRACE
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.thomas.core.context.SessionContextHolder.clearContext
import com.thomas.core.context.SessionContextHolder.context
import com.thomas.core.context.SessionContextHolder.currentUser
import com.thomas.core.context.SessionContextHolder.updateContext
import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.generator.SecurityUserGenerator.generateSecurityUser
import com.thomas.core.util.StringUtils.randomString
import com.thomas.spring.boot.security.SecurityUserAuthentication
import com.thomas.spring.boot.token.TokenDecrypter
import jakarta.servlet.DispatcherType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.context.SecurityContextHolder.getContext

class AuthenticationFilterTest {

    private lateinit var classLogger: Logger
    private lateinit var loggerAppender: ListAppender<ILoggingEvent>

    private val decrypterMock = mock<TokenDecrypter>()
    private val requestMock = mock<HttpServletRequest>()
    private val responseMock = mock<HttpServletResponse>()
    private val chainMock = mock<FilterChain>()

    private lateinit var filter: AuthenticationFilter

    @BeforeEach
    fun setup() {
        clearContext()
        getContext().authentication = null
        reset(requestMock, decrypterMock)
        filter = AuthenticationFilter(decrypterMock)
        doReturn(null).whenever(requestMock).getAttribute(any())
        doReturn(DispatcherType.REQUEST).whenever(requestMock).dispatcherType
        setupLogger(AuthenticationFilter::class.java.name)
    }

    private fun setupLogger(name: String) {
        classLogger = LoggerFactory.getLogger(name) as Logger
        classLogger.level = TRACE
        loggerAppender = ListAppender<ILoggingEvent>()
        loggerAppender.context = LoggerFactory.getILoggerFactory() as LoggerContext
        loggerAppender.start()
        classLogger.addAppender(loggerAppender)
    }

    @Test
    fun `Authentication filter should process token correctly`() {
        doReturn("Bearer ${randomString()}").whenever(requestMock).getHeader(AUTHORIZATION)
        doReturn(generateSecurityUser()).whenever(decrypterMock).decrypt(any())
        filter.doFilter(requestMock, responseMock, chainMock)
        assertEquals(currentUser, getContext().authentication?.details)
    }

    @Test
    fun `Authentication filter should clear security context on error`() {
        updateContext { it.copy(user = generateSecurityUser(), token = randomString(), currentOrganization = randomUUIDv7()) }
        getContext().authentication = SecurityUserAuthentication(currentUser, randomString(), true)

        doReturn("Bearer ${randomString()}").whenever(requestMock).getHeader(AUTHORIZATION)
        doThrow(RuntimeException("Error on decrypt")).whenever(decrypterMock).decrypt(any())
        assertThrows<Exception> { filter.doFilter(requestMock, responseMock, chainMock) }

        assertNull(context.currentUser)
        assertNull(context.currentToken)
        assertNull(context.currentOrganization)
    }

    @Test
    fun `Authentication filter should log default message when exception has no message`() {
        updateContext { it.copy(user = generateSecurityUser(), token = randomString(), currentOrganization = randomUUIDv7()) }
        getContext().authentication = SecurityUserAuthentication(currentUser, randomString(), true)

        doReturn("Bearer ${randomString()}").whenever(requestMock).getHeader(AUTHORIZATION)
        doThrow(RuntimeException()).whenever(decrypterMock).decrypt(any())
        assertThrows<Exception> { filter.doFilter(requestMock, responseMock, chainMock) }

        assertNull(context.currentUser)
        assertNull(context.currentToken)
        assertNull(context.currentOrganization)
    }

    @Test
    fun `Authentication filter should clear security context when token does not exists`() {
        updateContext { it.copy(user = generateSecurityUser(), token = randomString(), currentOrganization = randomUUIDv7()) }
        getContext().authentication = SecurityUserAuthentication(currentUser, randomString(), true)

        doReturn(null).whenever(requestMock).getHeader(AUTHORIZATION)
        filter.doFilter(requestMock, responseMock, chainMock)

        assertNull(context.currentUser)
        assertNull(context.currentToken)
        assertNull(context.currentOrganization)
    }

}
