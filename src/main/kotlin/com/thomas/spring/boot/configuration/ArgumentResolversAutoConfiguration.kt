package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.model.resolver.PageRequestPeriodResolver
import com.thomas.spring.boot.model.resolver.PageRequestResolver
import com.thomas.spring.boot.properties.PaginationProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@AutoConfiguration
@EnableConfigurationProperties(PaginationProperties::class)
class ArgumentResolversAutoConfiguration(
    private val paginationProperties: PaginationProperties
) : WebFluxConfigurer {

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(PageRequestPeriodResolver(paginationProperties.defaultPageNumber, paginationProperties.defaultPageSize))
        configurer.addCustomResolver(PageRequestResolver(paginationProperties.defaultPageNumber, paginationProperties.defaultPageSize))
    }

}
