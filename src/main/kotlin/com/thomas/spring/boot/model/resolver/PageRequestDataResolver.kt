package com.thomas.spring.boot.model.resolver

import com.thomas.core.model.pagination.PageRequestData
import com.thomas.core.model.pagination.PageSort
import com.thomas.core.model.pagination.PageSortDirection
import kotlin.reflect.KClass
import org.springframework.core.MethodParameter
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver

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

    protected fun pageNumber(request: NativeWebRequest) = parameterValue(
        request,
        PAGE_NUMBER_PARAM,
        defaultPageNumber,
    )

    protected fun pageSize(request: NativeWebRequest) = parameterValue(
        request,
        PAGE_SIZE_PARAM,
        defaultPageSize,
    )

    private fun parameterValue(
        request: NativeWebRequest,
        attr: String,
        default: Long,
    ): Long = request.getParameter(attr)?.let {
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

    protected fun sortList(request: NativeWebRequest): List<PageSort> = request.getParameterValues(
        SORT_ORDER_PARAM
    )?.map {
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
