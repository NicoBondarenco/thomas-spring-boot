package com.thomas.spring.boot.model.resolver

import com.thomas.core.model.pagination.PageRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer

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
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any = PageRequest(
        pageNumber = pageNumber(webRequest),
        pageSize = pageSize(webRequest),
        pageSort = sortList(webRequest),
    )

}
