package com.thomas.spring.boot.filter

import ch.qos.logback.classic.Level.TRACE
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.thomas.core.context.SessionContextHolder.clearContext
import com.thomas.core.context.SessionContextHolder.currentUnity
import com.thomas.core.context.SessionContextHolder.updateContext
import com.thomas.core.extension.randomUUIDv7
import jakarta.servlet.DispatcherType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder.getContext

class UnityFilterTest {

    private lateinit var classLogger: Logger
    private lateinit var loggerAppender: ListAppender<ILoggingEvent>

    private val requestMock = mock<HttpServletRequest>()
    private val responseMock = mock<HttpServletResponse>()
    private val chainMock = mock<FilterChain>()

    private lateinit var filter: UnityFilter

    @BeforeEach
    fun setup() {
        clearContext()
        getContext().authentication = null
        reset(requestMock)
        filter = UnityFilter()
        doReturn(null).whenever(requestMock).getAttribute(any())
        doReturn(DispatcherType.REQUEST).whenever(requestMock).dispatcherType
        setupLogger(UnityFilter::class.java.name)
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
    fun `Unity filter should process unity id correctly`() {
        val unityId = randomUUIDv7()
        doReturn(unityId.toString()).whenever(requestMock).getHeader("Current-Unity")
        filter.doFilter(requestMock, responseMock, chainMock)
        assertEquals(currentUnity, unityId)
    }

    @Test
    fun `Unity filter should process unity id correctly when null`() {
        updateContext { it.copy(currentUnity = randomUUIDv7()) }
        doReturn(null).whenever(requestMock).getHeader("Current-Unity")
        filter.doFilter(requestMock, responseMock, chainMock)
        assertNull(currentUnity)
    }

}
