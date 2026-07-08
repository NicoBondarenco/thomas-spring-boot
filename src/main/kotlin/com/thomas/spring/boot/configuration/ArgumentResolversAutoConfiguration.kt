package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.model.resolver.PageRequestPeriodResolver
import com.thomas.spring.boot.model.resolver.PageRequestResolver
import com.thomas.spring.boot.properties.PaginationProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@AutoConfiguration
@EnableConfigurationProperties(PaginationProperties::class)
class ArgumentResolversAutoConfiguration(
    private val paginationProperties: PaginationProperties
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(PageRequestPeriodResolver(paginationProperties.defaultPageNumber, paginationProperties.defaultPageSize))
        resolvers.add(PageRequestResolver(paginationProperties.defaultPageNumber, paginationProperties.defaultPageSize))
    }

}
