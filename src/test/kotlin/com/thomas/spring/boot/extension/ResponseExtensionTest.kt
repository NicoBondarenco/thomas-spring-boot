package com.thomas.spring.boot.extension

import com.thomas.core.exception.ApplicationException
import com.thomas.core.exception.ErrorType
import com.thomas.core.exception.ErrorType.Companion.APPLICATION_ERROR
import com.thomas.core.util.ErrorUtils.allGenericErrors
import com.thomas.core.util.StringUtils.randomString
import com.thomas.spring.boot.i18n.SpringMessageI18N.errorExceptionMappingExceptionResponseDefaultMessage
import java.net.URI
import java.time.ZonedDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

class ResponseExtensionTest {

    companion object {
        @JvmStatic
        fun errorTypes(): List<Arguments> = allGenericErrors().flatMap { errorType ->
            listOf(
                Arguments.of(errorType, null, null),
                Arguments.of(errorType, mapOf(randomString() to listOf(randomString())), null),
                Arguments.of(errorType, null, Exception(randomString())),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("errorTypes")
    fun `ApplicationException should map to response correctly`(
        errorType: ErrorType,
        details: Map<String, List<String>>?,
        cause: Throwable?
    ) {
        val uri = URI.create("/${randomString().replace(" ", "-")}")
        val message = randomString()
        val exception = TestApplicationException(message, errorType, details, cause)

        val response = exception.toProblemDetail(uri, errorType.toHttpStatus(INTERNAL_SERVER_ERROR))

        assertEquals(errorType.toHttpStatus(INTERNAL_SERVER_ERROR).value(), response.status)
        assertEquals(URI.create("/errors/${errorType.category}/${errorType.type}"), response.type)
        assertEquals(uri, response.instance)
        assertEquals(errorType.title, response.title)
        assertEquals(message, response.detail)
        assertNotNull(response.properties)
        assertTrue(response.properties!!.contains(PROBLEM_SPECIFICS_PROPERTY))
        assertTrue(response.properties!!.contains(PROBLEM_TIMESTAMP_PROPERTY))
        assertEquals(details, response.properties!![PROBLEM_SPECIFICS_PROPERTY])
        assertTrue(response.properties!![PROBLEM_TIMESTAMP_PROPERTY]!! is ZonedDateTime)
    }

    @Test
    fun `ApplicationException should map to response correctly with custom error type`() {
        val uri = URI.create("/${randomString().replace(" ", "-")}")
        val message = randomString()
        val errorValue = randomString(spaces = false)
        val errorType = ErrorType(errorValue.uppercase(), errorValue.lowercase(), errorValue.lowercase(), errorValue)
        val exception = TestApplicationException(message, errorType, null, null)

        val response = exception.toProblemDetail(uri, errorType.toHttpStatus(INTERNAL_SERVER_ERROR))

        assertEquals(errorType.toHttpStatus(INTERNAL_SERVER_ERROR).value(), response.status)
        assertEquals(URI.create("/errors/${errorType.category}/${errorType.type}"), response.type)
        assertEquals(uri, response.instance)
        assertEquals(errorType.title, response.title)
        assertEquals(message, response.detail)
        assertNotNull(response.properties)
        assertTrue(response.properties!!.contains(PROBLEM_SPECIFICS_PROPERTY))
        assertTrue(response.properties!!.contains(PROBLEM_TIMESTAMP_PROPERTY))
        assertNull(response.properties!![PROBLEM_SPECIFICS_PROPERTY])
        assertTrue(response.properties!![PROBLEM_TIMESTAMP_PROPERTY]!! is ZonedDateTime)
    }

    @Test
    fun `Exception with message should map to response correctly`() = testCommonException(randomString())

    @Test
    fun `Exception without message should map to response correctly`() = testCommonException(null)

    private fun testCommonException(message: String?) {
        val uri = URI.create("/${randomString().replace(" ", "-")}")
        val exception = IllegalArgumentException(message)
        val response = exception.toProblemDetail(uri, INTERNAL_SERVER_ERROR)

        val expected = message ?: errorExceptionMappingExceptionResponseDefaultMessage()

        assertEquals(INTERNAL_SERVER_ERROR.value(), response.status)
        assertEquals(URI.create(ERROR_DEFAULT_RESOURCE), response.type)
        assertEquals(uri, response.instance)
        assertEquals(APPLICATION_ERROR.title, response.title)
        assertEquals(expected, response.detail)
        assertNotNull(response.properties)
        assertTrue(response.properties!!.contains(PROBLEM_SPECIFICS_PROPERTY))
        assertTrue(response.properties!!.contains(PROBLEM_TIMESTAMP_PROPERTY))
        assertNull(response.properties!![PROBLEM_SPECIFICS_PROPERTY])
        assertTrue(response.properties!![PROBLEM_TIMESTAMP_PROPERTY]!! is ZonedDateTime)
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
