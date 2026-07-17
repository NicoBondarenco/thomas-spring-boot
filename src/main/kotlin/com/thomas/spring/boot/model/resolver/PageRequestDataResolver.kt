package com.thomas.spring.boot.model.resolver

import com.thomas.core.model.pagination.PageRequestData
import com.thomas.core.model.pagination.PageSort
import com.thomas.core.model.pagination.PageSortDirection
import kotlin.reflect.KClass
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver  // ← era .method.support
import org.springframework.web.server.ServerWebExchange

abstract class PageRequestDataResolver<T : PageRequestData>(
    private val klass: KClass<T>,
    private val defaultPageNumber: Long,
    private val defaultPageSize: Long,
) : HandlerMethodArgumentResolver {

    companion object {
        private const val PAGE_NUMBER_PARAM = "p"
        private const val PAGE_SIZE_PARAM = "s"
        private const val SORT_ORDER_PARAM = "o"
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean = parameter.parameterType == klass.java

    protected fun pageNumber(exchange: ServerWebExchange) = parameterValue(
        exchange,
        PAGE_NUMBER_PARAM,
        defaultPageNumber,
    )

    protected fun pageSize(exchange: ServerWebExchange) = parameterValue(
        exchange,
        PAGE_SIZE_PARAM,
        defaultPageSize,
    )

    private fun parameterValue(
        exchange: ServerWebExchange,
        attr: String,
        default: Long,
    ): Long = exchange.request.queryParams.getFirst(attr)?.let {
        parameterNumber(attr, it)
    } ?: default

    private fun parameterNumber(
        parameter: String,
        value: String,
    ): Long = try {
        value.toLong()
    } catch (e: NumberFormatException) {
        throw RequestParameterException(parameter, value)
    }

    protected fun sortList(
        exchange: ServerWebExchange
    ): List<PageSort> = exchange.request.queryParams[SORT_ORDER_PARAM]?.map {
        handleSortParameter(it)
    } ?: listOf()

    private fun handleSortParameter(
        parameter: String
    ): PageSort = try {
        parameter.split(",").filter {
            it.trim().isNotEmpty()
        }.let { (field, direction) ->
            PageSort(field, PageSortDirection.valueOf(direction.uppercase()))
        }
    } catch (e: Exception) {
        throw RequestParameterException(SORT_ORDER_PARAM, parameter)
    }

}
