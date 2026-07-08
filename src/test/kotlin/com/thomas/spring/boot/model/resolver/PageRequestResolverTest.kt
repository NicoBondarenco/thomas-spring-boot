package com.thomas.spring.boot.model.resolver

import com.thomas.core.context.SessionContext
import com.thomas.core.model.pagination.PageRequest
import com.thomas.core.model.pagination.PageSort
import com.thomas.core.model.pagination.PageSortDirection
import com.thomas.core.util.NumberUtils.randomLong
import com.thomas.core.util.StringUtils.randomString
import com.thomas.spring.boot.i18n.SpringMessageI18N.errorExceptionMappingRequestParameterInvalidParameter
import com.thomas.spring.boot.properties.PaginationProperties
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer

class PageRequestResolverTest {

    private val properties = PaginationProperties()
    private lateinit var resolver: PageRequestResolver

    private val parameter: MethodParameter = mockk()
    private val container: ModelAndViewContainer = mockk()
    private val request: NativeWebRequest = mockk()
    private val factory: WebDataBinderFactory = mockk()

    @BeforeEach
    fun setUp() {
        resolver = PageRequestResolver(properties.defaultPageNumber, properties.defaultPageSize)
    }

    @Test
    fun `Resolver should accept correctly class only`() {
        val correctParameter = mock<MethodParameter> {
            on { parameterType } doReturn PageRequest::class.java
        }
        val invalidParameter: MethodParameter = mock<MethodParameter> {
            on { parameterType } doReturn SessionContext::class.java
        }
        assertTrue(resolver.supportsParameter(correctParameter))
        assertFalse(resolver.supportsParameter(invalidParameter))
    }

    @Test
    fun `Should resolve all arguments correctly`() {
        val pageNumber: Long = randomLong(2, 50)
        val pageSize: Long = randomLong(11, 100)
        val pageSort: List<PageSort> = listOf(
            PageSort("created_at", PageSortDirection.DESC),
            PageSort("general_value", PageSortDirection.ASC),
        )
        configureRequest(
            pageNumber.toString(),
            pageSize.toString(),
            pageSort.asParameter(),
        )

        val pagination = resolver.resolveArgument(parameter, container, request, factory) as PageRequest
        assertEquals(pageNumber, pagination.pageNumber)
        assertEquals(pageSize, pagination.pageSize)
        assertEquals(pageSort, pagination.pageSort)
    }

    @Test
    fun `Should resolve all arguments null correctly`() {
        configureRequest()
        val pagination = resolver.resolveArgument(parameter, container, request, factory) as PageRequest
        assertEquals(properties.defaultPageNumber, pagination.pageNumber)
        assertEquals(properties.defaultPageSize, pagination.pageSize)
        assertTrue(pagination.pageSort.isEmpty())
    }

    @Test
    fun `Should throws exception when number is invalid`() {
        val value = randomString()
        configureRequest(pageNumber = value)
        val exception = assertThrows<RequestParameterException> { resolver.resolveArgument(parameter, container, request, factory) }
        assertEquals(errorExceptionMappingRequestParameterInvalidParameter("p", value), exception.message)
    }

    @Test
    fun `Should throws exception when sort is invalid`() {
        val value = randomString()
        configureRequest(pageSort = Array(1) { value })
        val exception = assertThrows<RequestParameterException> { resolver.resolveArgument(parameter, container, request, factory) }
        assertEquals(errorExceptionMappingRequestParameterInvalidParameter("o", value), exception.message)
    }

    @Test
    fun `Should throws exception when sort has invalid size`() {
        val value = " ,${randomString()}"
        configureRequest(pageSort = Array(1) { value })
        val exception = assertThrows<RequestParameterException> { resolver.resolveArgument(parameter, container, request, factory) }
        assertEquals(errorExceptionMappingRequestParameterInvalidParameter("o", value), exception.message)
    }

    private fun configureRequest(
        pageNumber: String? = null,
        pageSize: String? = null,
        pageSort: Array<String>? = null,
    ) {
        every { request.getParameter("p") } returns pageNumber
        every { request.getParameter("s") } returns pageSize
        every { request.getParameterValues("o") } returns pageSort
    }

    private fun List<PageSort>.asParameter(): Array<String> = this.map {
        "${it.sortField},${it.sortDirection}"
    }.toTypedArray()

}
