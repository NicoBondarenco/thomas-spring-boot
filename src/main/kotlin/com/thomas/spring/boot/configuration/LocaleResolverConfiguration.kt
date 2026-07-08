package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.model.resolver.ContextLocaleResolver
import com.thomas.spring.boot.model.resolver.RequestLocaleResolver
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.server.i18n.LocaleContextResolver
import org.springframework.web.servlet.LocaleResolver

@AutoConfiguration(before = [WebMvcAutoConfiguration::class])
class LocaleResolverConfiguration {

    @Bean
    fun localeContextResolver(): LocaleContextResolver = ContextLocaleResolver()

    @Bean
    fun localeResolver(): LocaleResolver = RequestLocaleResolver()

}
