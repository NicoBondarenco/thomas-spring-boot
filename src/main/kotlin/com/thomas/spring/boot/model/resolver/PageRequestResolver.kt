package com.thomas.spring.boot.model.resolver

import com.thomas.core.model.pagination.PageRequest
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class PageRequestResolver(
    defaultPageNumber: Long,
    defaultPageSize: Long,
) : PageRequestDataResolver<PageRequest>(
    PageRequest::class,
    defaultPageNumber,
    defaultPageSize,
) {

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> = Mono.just(
        PageRequest(
            pageNumber = pageNumber(exchange),
            pageSize = pageSize(exchange),
            pageSort = sortList(exchange),
        )
    )

}
