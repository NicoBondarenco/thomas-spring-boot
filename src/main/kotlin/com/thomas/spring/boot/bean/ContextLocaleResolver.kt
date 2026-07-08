package com.thomas.spring.boot.bean

import com.thomas.core.context.SessionContextHolder.currentLocale
import java.util.Locale
import org.springframework.context.i18n.LocaleContext
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver

class ContextLocaleResolver : AcceptHeaderLocaleContextResolver() {

    companion object {
        private val DEFAULT_LOCALE = Locale.ROOT
        private val SUPPORTED_LOCALES = listOf(Locale.ROOT, Locale.of("pt", "BR"), Locale.US)
    }

    init {
        defaultLocale = DEFAULT_LOCALE
        setSupportedLocales(SUPPORTED_LOCALES)
    }

    override fun resolveLocaleContext(
        exchange: ServerWebExchange
    ): LocaleContext = super.resolveLocaleContext(exchange).apply {
        currentLocale = this.locale!!
    }

}
