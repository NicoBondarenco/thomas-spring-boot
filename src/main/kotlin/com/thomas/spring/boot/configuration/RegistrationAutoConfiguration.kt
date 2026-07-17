package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.mapping.QueryMapping
import com.thomas.spring.boot.mapping.QueryRequestCondition
import java.lang.reflect.Method
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.webflux.autoconfigure.WebFluxRegistrations
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.reactive.result.condition.RequestCondition
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping


@AutoConfiguration
class RegistrationAutoConfiguration : WebFluxRegistrations {

    companion object {
        private val QUERY_MAPPING_ANNOTATIONS = setOf(QueryMapping::class.java)
    }

    override fun getRequestMappingHandlerMapping(): RequestMappingHandlerMapping {
        return object : RequestMappingHandlerMapping() {
            override fun getCustomMethodCondition(
                method: Method
            ): RequestCondition<*>? = AnnotatedElementUtils.findAllMergedAnnotations(
                method,
                QUERY_MAPPING_ANNOTATIONS
            ).takeIf { it.isNotEmpty() }?.let { QueryRequestCondition() }
        }
    }

}
