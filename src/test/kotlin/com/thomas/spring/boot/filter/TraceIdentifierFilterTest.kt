package com.thomas.spring.boot.filter

import ch.qos.logback.classic.Level.TRACE
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.thomas.core.context.SessionContextHolder.clearContext
import com.thomas.core.context.SessionContextHolder.traceIdentifier
import com.thomas.core.util.StringUtils.randomString
import jakarta.servlet.DispatcherType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder.getContext

class TraceIdentifierFilterTest {

    private lateinit var classLogger: Logger
    private lateinit var loggerAppender: ListAppender<ILoggingEvent>

    private val requestMock = mock<HttpServletRequest>()
    private val responseMock = mock<HttpServletResponse>()
    private val chainMock = mock<FilterChain>()

    private lateinit var filter: TraceIdentifierFilter

    @BeforeEach
    fun setup() {
        clearContext()
        getContext().authentication = null
        reset(requestMock)
        filter = TraceIdentifierFilter()
        doReturn(null).whenever(requestMock).getAttribute(any())
        doReturn(DispatcherType.REQUEST).whenever(requestMock).dispatcherType
        setupLogger(TraceIdentifierFilter::class.java.name)
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
    fun `Trace Identifier filter should process identifier correctly`() {
        val identifier = randomString()
        doReturn(identifier).whenever(requestMock).getHeader("Trace-Identifier")
        filter.doFilter(requestMock, responseMock, chainMock)
        assertEquals(traceIdentifier, identifier)
    }

    @Test
    fun `Trace Identifier filter should process identifier correctly when null`() {
        val identifier = traceIdentifier
        doReturn(null).whenever(requestMock).getHeader("Trace-Identifier")
        filter.doFilter(requestMock, responseMock, chainMock)
        assertEquals(traceIdentifier, identifier)
    }

}
