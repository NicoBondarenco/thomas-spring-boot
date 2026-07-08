package com.thomas.spring.boot.filter

import ch.qos.logback.classic.Level.TRACE
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.thomas.core.context.SessionContextHolder.clearContext
import com.thomas.core.context.SessionContextHolder.context
import com.thomas.core.context.SessionContextHolder.currentLocale
import com.thomas.core.context.SessionContextHolder.currentOrganization
import com.thomas.core.context.SessionContextHolder.currentToken
import com.thomas.core.context.SessionContextHolder.currentUnity
import com.thomas.core.context.SessionContextHolder.currentUser
import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.generator.SecurityUserGenerator.generateSecurityUser
import com.thomas.core.util.StringUtils.randomString
import jakarta.servlet.DispatcherType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder.getContext

class SessionContextLifecycleFilterTest {

    private lateinit var classLogger: Logger
    private lateinit var loggerAppender: ListAppender<ILoggingEvent>

    private val requestMock = mock<HttpServletRequest>()
    private val responseMock = mock<HttpServletResponse>()
    private val chainMock = mock<FilterChain>()

    private lateinit var filter: SessionContextLifecycleFilter

    @BeforeEach
    fun setup() {
        clearContext()
        getContext().authentication = null
        reset(requestMock)
        filter = SessionContextLifecycleFilter()
        doReturn(null).whenever(requestMock).getAttribute(any())
        doReturn(DispatcherType.REQUEST).whenever(requestMock).dispatcherType
        setupLogger(SessionContextLifecycleFilter::class.java.name)
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
    fun `Filter should clear context`() {
        currentUser = generateSecurityUser()
        currentToken = randomString()
        currentOrganization = randomUUIDv7()
        currentUnity = randomUUIDv7()
        currentLocale = Locale.FRANCE

        filter.doFilter(requestMock, responseMock, chainMock)

        assertNull(context.currentUser)
        assertNull(context.currentOrganization)
        assertNull(context.currentUnity)
        assertEquals(Locale.ROOT, context.currentLocale)
    }

}
