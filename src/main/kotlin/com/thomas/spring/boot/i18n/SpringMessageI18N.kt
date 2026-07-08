package com.thomas.spring.boot.i18n

import com.thomas.core.i18n.BundleResolver

internal object SpringMessageI18N : BundleResolver("strings/spring-strings") {

    fun filterAuthenticationFilterTokenDataEmptyToken(): String =
        formattedMessage("filter.authentication-filter.token-data.empty-token")

    fun filterAuthenticationFilterTokenDataEmptySignature(): String =
        formattedMessage("filter.authentication-filter.token-data.empty-signature")

    fun filterAuthenticationFilterTokenDataInvalidSignature(): String =
        formattedMessage("filter.authentication-filter.token-data.invalid-signature")

    fun errorExceptionMappingExceptionResponseDefaultMessage(): String =
        formattedMessage("error.exception-mapping.exception-response.default-message")

    fun errorExceptionMappingRequestParameterInvalidParameter(parameter: String, value: String): String =
        formattedMessage("error.exception-mapping.request-parameter.invalid-parameter", parameter, value)

}
