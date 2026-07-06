package com.thomas.spring.boot.i18n

import com.thomas.core.i18n.BundleResolver

internal object SpringMessageI18N : BundleResolver("strings/spring-strings") {

    fun filterAuthenticationFilterTokenDataEmptyToken(): String = formattedMessage("filter.authentication-filter.token-data.empty-token")

    fun filterAuthenticationFilterTokenDataEmptySignature(): String = formattedMessage("filter.authentication-filter.token-data.empty-signature")

    fun filterAuthenticationFilterTokenDataInvalidSignature(): String = formattedMessage("filter.authentication-filter.token-data.invalid-signature")

    fun filterAuthenticationFilterSecurityContextDecryptionError(
        token: String,
        error: String,
    ): String = formattedMessage("filter.authentication-filter.security-context.decryption-error", token, error)

    fun filterAuthenticationFilterSecurityContextDefaultError(): String = formattedMessage("filter.authentication-filter.security-context.default-error")

}
