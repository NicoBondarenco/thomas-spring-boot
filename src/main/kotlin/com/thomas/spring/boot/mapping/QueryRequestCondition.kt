package com.thomas.spring.boot.mapping

import com.thomas.spring.boot.extension.QUERY
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.servlet.mvc.condition.RequestCondition

class QueryRequestCondition : RequestCondition<QueryRequestCondition> {

    override fun combine(other: QueryRequestCondition): QueryRequestCondition = this

    override fun getMatchingCondition(
        request: HttpServletRequest
    ): QueryRequestCondition? = this.takeIf {
        QUERY.name().equals(request.method, ignoreCase = true)
    }

    override fun compareTo(
        other: QueryRequestCondition,
        request: HttpServletRequest
    ): Int = 0

}
