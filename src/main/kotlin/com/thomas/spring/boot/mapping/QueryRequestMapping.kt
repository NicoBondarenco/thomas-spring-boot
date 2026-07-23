package com.thomas.spring.boot.mapping

import java.lang.reflect.Method
import org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation
import org.springframework.web.reactive.result.condition.RequestCondition
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping


class QueryRequestMapping : RequestMappingHandlerMapping() {

    override fun getCustomMethodCondition(
        method: Method
    ): RequestCondition<*>? = findMergedAnnotation(method, QueryMapping::class.java)?.let {
        QueryRequestCondition()
    }

}
