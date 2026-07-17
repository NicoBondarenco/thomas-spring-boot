package com.thomas.spring.boot.mapping

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull

class QueryRequestConditionTest {

    private val request: HttpServletRequest = mockk()

    private lateinit var condition: QueryRequestCondition

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        condition = QueryRequestCondition()
    }

    @Test
    fun `Query condition should return itself on combine`() {
        val result = condition.combine(QueryRequestCondition())
        assertEquals(condition, result)
    }

    @Test
    fun `Query condition should return itself if request http method is QUERY`() {
        every { request.method } returns "QUERY"
        assertEquals(condition, condition.getMatchingCondition(request))
    }

    @Test
    fun `Query condition should return null if request http method is not QUERY`() {
        every { request.method } returns null
        assertNull(condition.getMatchingCondition(request))
    }

    @Test
    fun `Query condition comparison should always returns zero`() {
        assertEquals(0, condition.compareTo(QueryRequestCondition(), request))
    }

}
