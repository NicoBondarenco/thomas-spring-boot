package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.model.resolver.ContextLocaleResolver
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.webflux.autoconfigure.WebFluxAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.server.i18n.LocaleContextResolver

@AutoConfiguration(before = [WebFluxAutoConfiguration::class])
class LocaleResolverConfiguration {

    @Bean
    fun localeContextResolver(): LocaleContextResolver = ContextLocaleResolver()


}
