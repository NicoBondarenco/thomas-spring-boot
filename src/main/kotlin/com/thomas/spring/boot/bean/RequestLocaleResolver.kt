package com.thomas.spring.boot.bean

import com.thomas.core.context.SessionContextHolder.currentLocale
import jakarta.servlet.http.HttpServletRequest
import java.util.Locale
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver

class RequestLocaleResolver : AcceptHeaderLocaleResolver() {

    companion object {
        private val DEFAULT_LOCALE = Locale.ROOT
        private val SUPPORTED_LOCALES = listOf(Locale.ROOT, Locale.of("pt", "BR"), Locale.US)
    }

    init {
        defaultLocale = DEFAULT_LOCALE
        setSupportedLocales(SUPPORTED_LOCALES)
    }

    override fun resolveLocale(
        request: HttpServletRequest
    ): Locale = super.resolveLocale(request).apply {
        currentLocale = this
    }

}
