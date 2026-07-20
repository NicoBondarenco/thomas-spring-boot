package com.thomas.spring.boot.filter

import ch.qos.logback.classic.Level.TRACE
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.thomas.core.context.SessionContextHolder
import com.thomas.core.context.SessionContextHolder.traceIdentifier
import com.thomas.core.util.StringUtils.randomString
import com.thomas.spring.boot.extension.clearRequestContext
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class TraceIdentifierFilterTest {

    private lateinit var classLogger: Logger
    private lateinit var loggerAppender: ListAppender<ILoggingEvent>

    private val exchangeMock = mockk<ServerWebExchange>()
    private val requestMock = mockk<ServerHttpRequest>()
    private val chainMock = mockk<WebFilterChain>()
    private val requestHeaders = HttpHeaders()

    private lateinit var filter: TraceIdentifierFilter

    @BeforeEach
    fun setup() {
        SessionContextHolder.clearRequestContext()
        clearAllMocks()
        filter = TraceIdentifierFilter()
        setupLogger(TraceIdentifierFilter::class.java.name)
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
    fun `Trace Identifier filter should process identifier correctly`() {
        val identifier = randomString()
        requestHeaders["Trace-Identifier"] = identifier
        filter.filter(exchangeMock, chainMock)
        assertEquals(traceIdentifier, identifier)
    }

    @Test
    fun `Trace Identifier filter should process identifier correctly when null`() {
        val identifier = traceIdentifier
        filter.filter(exchangeMock, chainMock)
        assertEquals(traceIdentifier, identifier)
    }

}
