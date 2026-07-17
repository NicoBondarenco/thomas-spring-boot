package com.thomas.spring.boot.mapping

import com.thomas.spring.boot.extension.QUERY
import org.springframework.web.reactive.result.condition.RequestCondition  // ← era .servlet.mvc.condition
import org.springframework.web.server.ServerWebExchange

class QueryRequestCondition : RequestCondition<QueryRequestCondition> {

    override fun combine(other: QueryRequestCondition): QueryRequestCondition = this

    override fun getMatchingCondition(
        exchange: ServerWebExchange
    ): QueryRequestCondition? = this.takeIf {
        QUERY == exchange.request.method
    }

    override fun compareTo(
        other: QueryRequestCondition,
        exchange: ServerWebExchange
    ): Int = 0

}
