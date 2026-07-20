package com.thomas.spring.boot.model.resolver

import com.thomas.core.context.SessionContext
import com.thomas.core.extension.toIsoOffsetDateTime
import com.thomas.core.model.pagination.PageRequestPeriod
import com.thomas.core.model.pagination.PageSort
import com.thomas.core.model.pagination.PageSortDirection
import com.thomas.core.util.NumberUtils.randomLong
import com.thomas.core.util.StringUtils.randomString
import com.thomas.spring.boot.i18n.SpringMessageI18N.errorExceptionMappingRequestParameterInvalidParameter
import com.thomas.spring.boot.properties.PaginationProperties
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.time.OffsetDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.core.MethodParameter
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.BindingContext
import org.springframework.web.server.ServerWebExchange

class PageRequestPeriodResolverTest {

    private val properties = PaginationProperties(0, 10)
    private lateinit var resolver: PageRequestPeriodResolver

    private val parameter: MethodParameter = mockk()
    private val context: BindingContext = mockk()
    private val exchange: ServerWebExchange = mockk()
    private val request: ServerHttpRequest = mockk()
    private val parameters: MultiValueMap<String, String> = LinkedMultiValueMap()

    @BeforeEach
    fun setUp() {
        resolver = PageRequestPeriodResolver(properties.defaultPageNumber, properties.defaultPageSize)
        clearAllMocks()
        parameters.clear()
    }

    @Test
    fun `Resolver should accept correctly class only`() {
        val correctParameter = mock<MethodParameter> {
            on { parameterType } doReturn PageRequestPeriod::class.java
        }
        val invalidParameter: MethodParameter = mock<MethodParameter> {
            on { parameterType } doReturn SessionContext::class.java
        }
        assertTrue(resolver.supportsParameter(correctParameter))
        assertFalse(resolver.supportsParameter(invalidParameter))
    }

    @Test
    fun `Should resolve all arguments correctly`() {
        val createdStart: OffsetDateTime = OffsetDateTime.now().minusDays(4)
        val createdEnd: OffsetDateTime = OffsetDateTime.now().minusDays(3)
        val updatedStart: OffsetDateTime = OffsetDateTime.now().minusDays(2)
        val updatedEnd: OffsetDateTime = OffsetDateTime.now().minusDays(1)
        val pageNumber: Long = randomLong(2, 50)
        val pageSize: Long = randomLong(11, 100)
        val pageSort: List<PageSort> = listOf(
            PageSort("created_at", PageSortDirection.DESC),
            PageSort("general_value", PageSortDirection.ASC),
        )
        configureRequest(
            createdStart.toIsoOffsetDateTime(),
            createdEnd.toIsoOffsetDateTime(),
            updatedStart.toIsoOffsetDateTime(),
            updatedEnd.toIsoOffsetDateTime(),
            pageNumber.toString(),
            pageSize.toString(),
            pageSort.asParameter(),
        )

        val pagination = resolver.resolveArgument(parameter, context, exchange).block() as PageRequestPeriod
        assertEquals(createdStart, pagination.createdStart)
        assertEquals(createdEnd, pagination.createdEnd)
        assertEquals(updatedStart, pagination.updatedStart)
        assertEquals(updatedEnd, pagination.updatedEnd)
        assertEquals(pageNumber, pagination.pageNumber)
        assertEquals(pageSize, pagination.pageSize)
        assertEquals(pageSort, pagination.pageSort)
    }

    @Test
    fun `Should resolve all arguments null correctly`() {
        configureRequest()
        val pagination = resolver.resolveArgument(parameter, context, exchange).block() as PageRequestPeriod
        assertNull(pagination.createdStart)
        assertNull(pagination.createdEnd)
        assertNull(pagination.updatedStart)
        assertNull(pagination.updatedEnd)
        assertEquals(properties.defaultPageNumber, pagination.pageNumber)
        assertEquals(properties.defaultPageSize, pagination.pageSize)
        assertTrue(pagination.pageSort.isEmpty())
    }

    @Test
    fun `Should throws exception when date is invalid`() {
        val value = randomString()
        configureRequest(createdStart = value)
        val exception = assertThrows<RequestParameterException> { resolver.resolveArgument(parameter, context, exchange).block() }
        assertEquals(errorExceptionMappingRequestParameterInvalidParameter("cs", value), exception.message)
    }

    @Test
    fun `Should throws exception when number is invalid`() {
        val value = randomString()
        configureRequest(pageNumber = value)
        val exception = assertThrows<RequestParameterException> { resolver.resolveArgument(parameter, context, exchange).block() }
        assertEquals(errorExceptionMappingRequestParameterInvalidParameter("p", value), exception.message)
    }

    @Test
    fun `Should throws exception when sort is invalid`() {
        val value = randomString()
        configureRequest(pageSort = Array(1) { value })
        val exception = assertThrows<RequestParameterException> {
            resolver.resolveArgument(parameter, context, exchange)
        }
        assertEquals(errorExceptionMappingRequestParameterInvalidParameter("o", value), exception.message)
    }

    @Test
    fun `Should throws exception when sort has invalid size`() {
        val value = " ,${randomString()}"
        configureRequest(pageSort = Array(1) { value })
        val exception = assertThrows<RequestParameterException> { resolver.resolveArgument(parameter, context, exchange) }
        assertEquals(errorExceptionMappingRequestParameterInvalidParameter("o", value), exception.message)
    }

    private fun configureRequest(
        createdStart: String? = null,
        createdEnd: String? = null,
        updatedStart: String? = null,
        updatedEnd: String? = null,
        pageNumber: String? = null,
        pageSize: String? = null,
        pageSort: Array<String>? = null,
    ) {
        every { exchange.request } returns request
        every { request.queryParams } returns parameters
        parameters["p"] = pageNumber
        parameters["s"] = pageSize
        parameters.addAll("o", pageSort?.toList() ?: emptyList())
        parameters["cs"] = createdStart
        parameters["ce"] = createdEnd
        parameters["us"] = updatedStart
        parameters["ue"] = updatedEnd
    }

    private fun List<PageSort>.asParameter(): Array<String> = this.map {
        "${it.sortField},${it.sortDirection}"
    }.toTypedArray()

}
