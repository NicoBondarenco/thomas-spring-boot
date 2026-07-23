package com.thomas.spring.boot.mapping

import com.thomas.core.context.SessionContextHolder
import com.thomas.spring.boot.extension.QUERY
import com.thomas.spring.boot.extension.clearRequestContext
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.springframework.http.HttpMethod.GET
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange

class QueryRequestConditionTest {

    private val exchange: ServerWebExchange = mockk()
    private val request: ServerHttpRequest = mockk()

    private lateinit var condition: QueryRequestCondition

    @BeforeEach
    fun setUp() {
        SessionContextHolder.clearRequestContext()
        clearAllMocks()
        condition = QueryRequestCondition()
        setupRequest()
    }

    private fun setupRequest() {
        every { exchange.request } returns request
    }

    @Test
    fun `Query condition should return itself on combine`() {
        val result = condition.combine(QueryRequestCondition())
        assertEquals(condition, result)
    }

    @Test
    fun `Query condition should return itself if request http method is QUERY`() {
        every { request.method } returns QUERY
        assertEquals(condition, condition.getMatchingCondition(exchange))
    }

    @Test
    fun `Query condition should return null if request http method is not QUERY`() {
        every { request.method } returns GET
        assertNull(condition.getMatchingCondition(exchange))
    }

    @Test
    fun `Query condition comparison should always returns zero`() {
        assertEquals(0, condition.compareTo(QueryRequestCondition(), exchange))
    }

}
