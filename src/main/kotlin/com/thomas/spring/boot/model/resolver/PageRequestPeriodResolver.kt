package com.thomas.spring.boot.model.resolver

import com.thomas.core.model.pagination.PageRequestPeriod
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class PageRequestPeriodResolver(
    defaultPageNumber: Long,
    defaultPageSize: Long,
) : PageRequestDataResolver<PageRequestPeriod>(
    PageRequestPeriod::class,
    defaultPageNumber,
    defaultPageSize,
) {

    companion object {
        private const val CREATED_START_PARAM = "cs"
        private const val CREATED_END_PARAM = "ce"
        private const val UPDATED_START_PARAM = "us"
        private const val UPDATED_END_PARAM = "ue"

        private val FORMATTER = ISO_OFFSET_DATE_TIME
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> = Mono.just(
        PageRequestPeriod(
            createdStart = dateTimeParameter(exchange, CREATED_START_PARAM),
            createdEnd = dateTimeParameter(exchange, CREATED_END_PARAM),
            updatedStart = dateTimeParameter(exchange, UPDATED_START_PARAM),
            updatedEnd = dateTimeParameter(exchange, UPDATED_END_PARAM),
            pageNumber = pageNumber(exchange),
            pageSize = pageSize(exchange),
            pageSort = sortList(exchange),
        )
    )

    private fun dateTimeParameter(
        exchange: ServerWebExchange,
        parameter: String,
    ): OffsetDateTime? = exchange.request.queryParams.getFirst(parameter)?.let {
        try {
            OffsetDateTime.parse(it, FORMATTER)
        } catch (_: Exception) {
            throw RequestParameterException(parameter, it)
        }
    }

}
