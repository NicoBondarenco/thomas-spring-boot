package com.thomas.spring.boot.bean

import com.thomas.core.context.SessionContextHolder.currentLocale
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import java.util.Locale
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.servlet.i18n.AbstractLocaleResolver

class RequestLocaleResolverTest {

    private lateinit var resolver: RequestLocaleResolver
    private val request: HttpServletRequest = mockk()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        resolver = RequestLocaleResolver()
    }

    @Test
    fun `ContextLocaleResolver should return default locale ROOT`() {
        val getter = AbstractLocaleResolver::class.memberProperties.first { it.name == "defaultLocale" }.getter
        getter.isAccessible = true
        assertEquals(Locale.ROOT, getter.call(resolver))
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
        every { request.getHeader(any()) } returns "pt-BR"
        every { request.locale } returns Locale.of("pt", "BR")
        currentLocale = Locale.CANADA
        resolver.resolveLocale(request)
        assertEquals(Locale.of("pt", "BR"), currentLocale)
    }

}
