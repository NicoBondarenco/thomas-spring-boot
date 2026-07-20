package com.thomas.spring.boot.filter

import ch.qos.logback.classic.Level.TRACE
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.thomas.core.context.SessionContextHolder
import com.thomas.core.context.SessionContextHolder.context
import com.thomas.core.context.SessionContextHolder.currentLocale
import com.thomas.core.context.SessionContextHolder.currentOrganization
import com.thomas.core.context.SessionContextHolder.currentToken
import com.thomas.core.context.SessionContextHolder.currentUnity
import com.thomas.core.context.SessionContextHolder.currentUser
import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.generator.SecurityUserGenerator.generateSecurityUser
import com.thomas.core.util.StringUtils.randomString
import com.thomas.spring.boot.extension.clearRequestContext
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.slf4j.LoggerFactory
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class SessionContextLifecycleFilterTest {

    private lateinit var classLogger: Logger
    private lateinit var loggerAppender: ListAppender<ILoggingEvent>

    private val exchangeMock = mockk<ServerWebExchange>()
    private val chainMock = mockk<WebFilterChain>()

    private lateinit var filter: SessionContextLifecycleFilter

    @BeforeEach
    fun setup() {
        SessionContextHolder.clearRequestContext()
        clearAllMocks()
        filter = SessionContextLifecycleFilter()
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

        every { chainMock.filter(any()) } returns Mono.empty()

        filter.filter(exchangeMock, chainMock).block()

        assertNull(context.currentUser)
        assertNull(context.currentOrganization)
        assertNull(context.currentUnity)
        assertEquals(Locale.ROOT, context.currentLocale)
    }

}
