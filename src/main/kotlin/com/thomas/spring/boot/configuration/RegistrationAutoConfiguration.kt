package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.mapping.QueryMapping
import com.thomas.spring.boot.mapping.QueryMappingJson
import com.thomas.spring.boot.mapping.QueryRequestCondition
import java.lang.reflect.Method
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.servlet.mvc.condition.RequestCondition
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@AutoConfiguration
class RegistrationAutoConfiguration : WebMvcRegistrations {

    companion object {
        private val QUERY_MAPPING_ANNOTATIONS = setOf(QueryMappingJson::class.java, QueryMapping::class.java)
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
