package com.thomas.spring.boot.handler

import com.thomas.core.util.NumberUtils.randomInteger
import com.thomas.core.util.StringUtils.randomString
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import jakarta.validation.Validator
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.lang.reflect.Method
import java.net.URI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.validation.beanvalidation.MethodValidationAdapter
import org.springframework.validation.method.MethodValidationException
import org.springframework.web.server.ServerWebExchange

class SpringBootExceptionHandlerTest {

    private val exchange: ServerWebExchange = mockk()
    private val request: ServerHttpRequest = mockk()
    private val response: ServerHttpResponse = mockk()

    private lateinit var handler: SpringBootExceptionHandler
    private lateinit var validator: Validator
    private lateinit var adapter: MethodValidationAdapter
    private lateinit var target: ValidatorController
    private lateinit var method: Method

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        handler = SpringBootExceptionHandler()
        validator = LocalValidatorFactoryBean().apply { afterPropertiesSet() }
        adapter = MethodValidationAdapter(validator)
        target = ValidatorController()
        method = ValidatorController::class.java.getDeclaredMethod("validatorMethod", String::class.java, Int::class.java)
        every { exchange.request } returns request
        every { exchange.response } returns response
        every { request.uri } returns URI.create("/${randomString(spaces = false)}")
        every { response.isCommitted } returns false
    }

    @Test
    fun `Handle general exceptions`() {
        val result = handler.handleUncaughtException(RuntimeException(randomString()), exchange)
        val problem = result.block()?.body
        assertNotNull(problem)
    }

    @Test
    fun `Handle exception with body`() {
        val args = arrayOf<Any>(randomString(5), randomInteger(11, 100))

        val validation = adapter.validateArguments(target, method, null, args, emptyArray<Class<*>>())
        val exception = MethodValidationException(validation)

        val result = handler.handleException(exception, exchange)
        val problem = result.block()?.body
        assertNotNull(problem)
    }

    private class ValidatorController {
        fun validatorMethod(
            @NotBlank
            @Size(min = 10)
            name: String,
            @Max(10)
            quantity: Int,
        ) {
        }
    }

    private data class ValidateRequest(
        @get:NotBlank
        @get:Size(min = 10)
        val name: String,
        @get:Max(10)
        val quantity: Int,
    )


}
