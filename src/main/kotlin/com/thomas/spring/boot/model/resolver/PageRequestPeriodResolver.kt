package com.thomas.spring.boot.model.resolver

import com.thomas.core.model.pagination.PageRequestPeriod
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer

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
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any = PageRequestPeriod(
        createdStart = dateTimeParameter(webRequest, CREATED_START_PARAM),
        createdEnd = dateTimeParameter(webRequest, CREATED_END_PARAM),
        updatedStart = dateTimeParameter(webRequest, UPDATED_START_PARAM),
        updatedEnd = dateTimeParameter(webRequest, UPDATED_END_PARAM),
        pageNumber = pageNumber(webRequest),
        pageSize = pageSize(webRequest),
        pageSort = sortList(webRequest),
    )

    private fun dateTimeParameter(
        request: NativeWebRequest,
        parameter: String,
    ): OffsetDateTime? = request.getParameter(parameter)?.let {
        try {
            OffsetDateTime.parse(it, FORMATTER)
        } catch (_: Exception) {
            throw RequestParameterException(parameter, it)
        }
    }

}
