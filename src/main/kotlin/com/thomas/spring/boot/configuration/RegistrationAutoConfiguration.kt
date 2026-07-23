package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.mapping.QueryRequestMapping
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.webflux.autoconfigure.WebFluxRegistrations
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping


@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@AutoConfiguration
class RegistrationAutoConfiguration : WebFluxRegistrations {

    override fun getRequestMappingHandlerMapping(): RequestMappingHandlerMapping = QueryRequestMapping()

}
