package com.thomas.spring.boot.bean

import com.thomas.core.context.SessionContextHolder.currentLocale
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange

class ContextLocaleResolverTest {

    private lateinit var resolver: ContextLocaleResolver
    private val exchange: ServerWebExchange = mockk()
    private val request: ServerHttpRequest = mockk()
    private val headers: HttpHeaders = mockk()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        resolver = ContextLocaleResolver()
    }

    @Test
    fun `ContextLocaleResolver should return default locale ROOT`() {
        assertEquals(Locale.ROOT, resolver.defaultLocale)
    }

    @Test
    fun `ContextLocaleResolver should have the correct supported locales`() {
        val locales = listOf(Locale.ROOT, Locale.of("pt", "BR"), Locale.US)
        assertEquals(locales.size, resolver.supportedLocales.size)
        locales.forEach { locale ->
            assertTrue(resolver.supportedLocales.contains(locale))
        }
    }

    @Test
    fun `ContextLocaleResolver should set the correct currentLocale`() {
        every { exchange.request } returns request
        every { request.headers } returns headers
        every { headers.acceptLanguageAsLocales } returns listOf(Locale.of("pt", "BR"))

        currentLocale = Locale.CANADA

        resolver.resolveLocaleContext(exchange)

        assertEquals(Locale.of("pt", "BR"), currentLocale)
    }

}
