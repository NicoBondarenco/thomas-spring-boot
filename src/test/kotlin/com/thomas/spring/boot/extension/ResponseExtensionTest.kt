package com.thomas.spring.boot.extension

import com.thomas.core.exception.ApplicationException
import com.thomas.core.exception.ErrorType
import com.thomas.core.util.StringUtils.randomString
import com.thomas.spring.boot.i18n.SpringMessageI18N.errorExceptionMappingExceptionResponseDefaultMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

class ResponseExtensionTest {

    companion object {
        @JvmStatic
        fun errorTypes(): List<Arguments> = ErrorType.entries.flatMap { errorType ->
            errorType.let {
                listOf(
                    Arguments.of(it, null, null),
                    Arguments.of(it, mapOf(randomString() to listOf(randomString())), null),
                    Arguments.of(it, null, Exception(randomString())),
                )
            }
        }
    }

    @ParameterizedTest
    @MethodSource("errorTypes")
    fun `ApplicationException should map to response correctly`(
        errorType: ErrorType,
        details: Map<String, List<String>>?,
        cause: Throwable?
    ) {
        val uri = randomString()
        val message = randomString()
        val status = errorType.toHttpStatus()
        val exception = TestApplicationException(message, errorType, details, cause)

        val response = exception.toExceptionResponse(uri)

        assertEquals(status, response.status)
        assertEquals(status.value(), response.code)
        assertEquals(uri, response.path)
        assertEquals(message, response.message)
        assertEquals(details, response.detail)
    }

    @Test
    fun `Exception with message should map to response correctly`() = testCommonException(randomString())

    @Test
    fun `Exception without message should map to response correctly`() = testCommonException(null)

    private fun testCommonException(message: String?) {
        val uri = randomString()
        val exception = IllegalArgumentException(message)
        val response = exception.toExceptionResponse(uri)

        val expected = message ?: errorExceptionMappingExceptionResponseDefaultMessage()

        assertEquals(INTERNAL_SERVER_ERROR, response.status)
        assertEquals(INTERNAL_SERVER_ERROR.value(), response.code)
        assertEquals(uri, response.path)
        assertEquals(expected, response.message)
        assertNull(response.detail)
    }

    class TestApplicationException(
        message: String,
        type: ErrorType,
        detail: Map<String, List<String>>?,
        cause: Throwable?
    ) : ApplicationException(
        message = message,
        type = type,
        detail = detail,
        cause = cause,
    )

}
