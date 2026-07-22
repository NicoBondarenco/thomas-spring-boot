package com.thomas.spring.boot.token

import com.thomas.core.extension.EMPTY_STRING
import com.thomas.core.model.security.SecurityRole
import com.thomas.core.model.security.SecurityUnity
import com.thomas.core.model.security.SecurityUser
import com.thomas.core.model.security.SecurityUserType
import com.thomas.spring.boot.i18n.SpringMessageI18N.filterAuthenticationFilterTokenDataEmptySignature
import com.thomas.spring.boot.i18n.SpringMessageI18N.filterAuthenticationFilterTokenDataEmptyToken
import com.thomas.spring.boot.i18n.SpringMessageI18N.filterAuthenticationFilterTokenDataInvalidSignature
import com.thomas.spring.boot.properties.TokenDecrypterProperties
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TokenDecrypterTest {

    private val encryptionAlgorithm = "HmacSHA256"
    private val encryptionKey = "dc6a395f-7b1d-4426-85e9-6347b9c3fccc"

    @Test
    fun `Encrypt security user correctly`() {
        val expected = "v2Z1c2VySWR4JGNiMDc5ZGVkLTI2NDQtNDk0YS1iYjc1LTlmNGQ4OTJmZWEwMWh1c2VyVHlwZWZDT01NT05oaXNBY3Rpdm" +
            "X1bm9yZ2FuaXphdGlvbklkeCQ5ZjhlZmI3OS1iMjA2LTQ0NTMtYjViNC00ZjQwYjM4NGFlY2Nxb3JnYW5pemF0aW9uUm9sZXOfa1JPTEV" +
            "fQ09NTU9Oa1JPTEVfU0VDT05E/29zZWN1cml0eVVuaXRpZXOfv2d1bml0eUlkeCRlMDliNThhYS1lN2YxLTQ5NjktYWVhMy1kMzlmYWU0" +
            "OWViMGRqdW5pdHlSb2xlc59qUk9MRV9VTklUWWpST0xFX0FETUlO/////w==.og8CuLpjUL2bTGmg03oCNtX+/LDoFGqSmt/BE4H+slQ="
        val properties = TokenDecrypterProperties(encryptionAlgorithm, encryptionKey)
        val decrypter = TokenDecrypter(properties)
        val user = SecurityUser(
            userId = UUID.fromString("cb079ded-2644-494a-bb75-9f4d892fea01"),
            userType = SecurityUserType("COMMON"),
            isActive = true,
            organizationId = UUID.fromString("9f8efb79-b206-4453-b5b4-4f40b384aecc"),
            organizationRoles = setOf(SecurityRole("ROLE_COMMON"), SecurityRole("ROLE_SECOND")),
            securityUnities = setOf(
                SecurityUnity(
                    unityId = UUID.fromString("e09b58aa-e7f1-4969-aea3-d39fae49eb0d"),
                    unityRoles = setOf(SecurityRole("ROLE_UNITY"), SecurityRole("ROLE_ADMIN")),
                ),
            ),
        )
        val token = decrypter.encrypt(user)
        assertEquals(expected, token)
    }

    @Test
    fun `Decrypt security user correctly`() {
        val token = "v2Z1c2VySWR4JGNiMDc5ZGVkLTI2NDQtNDk0YS1iYjc1LTlmNGQ4OTJmZWEwMWh1c2VyVHlwZWZDT01NT05oaXNBY3Rpdm" +
            "X1bm9yZ2FuaXphdGlvbklkeCQ5ZjhlZmI3OS1iMjA2LTQ0NTMtYjViNC00ZjQwYjM4NGFlY2Nxb3JnYW5pemF0aW9uUm9sZXOfa1JPTEV" +
            "fQ09NTU9Oa1JPTEVfU0VDT05E/29zZWN1cml0eVVuaXRpZXOfv2d1bml0eUlkeCRlMDliNThhYS1lN2YxLTQ5NjktYWVhMy1kMzlmYWU0" +
            "OWViMGRqdW5pdHlSb2xlc59qUk9MRV9VTklUWWpST0xFX0FETUlO/////w==.og8CuLpjUL2bTGmg03oCNtX+/LDoFGqSmt/BE4H+slQ="
        val properties = TokenDecrypterProperties(encryptionAlgorithm, encryptionKey)
        val decrypter = TokenDecrypter(properties)
        val user = SecurityUser(
            userId = UUID.fromString("cb079ded-2644-494a-bb75-9f4d892fea01"),
            userType = SecurityUserType("COMMON"),
            isActive = true,
            organizationId = UUID.fromString("9f8efb79-b206-4453-b5b4-4f40b384aecc"),
            organizationRoles = setOf(SecurityRole("ROLE_COMMON"), SecurityRole("ROLE_SECOND")),
            securityUnities = setOf(
                SecurityUnity(
                    unityId = UUID.fromString("e09b58aa-e7f1-4969-aea3-d39fae49eb0d"),
                    unityRoles = setOf(SecurityRole("ROLE_UNITY"), SecurityRole("ROLE_ADMIN")),
                ),
            ),
        )
        val result = decrypter.decrypt(token)
        assertEquals(user, result)
    }

    @Test
    fun `Decrypt security user with empty token`() {
        val token = EMPTY_STRING
        val properties = TokenDecrypterProperties(encryptionAlgorithm, encryptionKey)
        val decrypter = TokenDecrypter(properties)
        val exception = assertThrows<TokenDecryptionException> { decrypter.decrypt(token) }
        assertEquals(filterAuthenticationFilterTokenDataEmptyToken(), exception.message)
    }

    @Test
    fun `Decrypt security user with blank token`() {
        val token = "    "
        val properties = TokenDecrypterProperties(encryptionAlgorithm, encryptionKey)
        val decrypter = TokenDecrypter(properties)
        val exception = assertThrows<TokenDecryptionException> { decrypter.decrypt(token) }
        assertEquals(filterAuthenticationFilterTokenDataEmptyToken(), exception.message)
    }

    @Test
    fun `Decrypt security user without signature`() {
        val token = "v2Z1c2VySWR4JGNiMDc5ZGVkLTI2NDQtNDk0YS1iYjc1LTlmNGQ4OTJmZWEwMWh1c2VyVHlwZWZDT01NT05oaXNBY3Rpdm" +
            "X1bm9yZ2FuaXphdGlvbklkeCQ5ZjhlZmI3OS1iMjA2LTQ0NTMtYjViNC00ZjQwYjM4NGFlY2Nxb3JnYW5pemF0aW9uUm9sZXOfa1JPTEV" +
            "fQ09NTU9Oa1JPTEVfU0VDT05E/29zZWN1cml0eVVuaXRpZXOfv2d1bml0eUlkeCRlMDliNThhYS1lN2YxLTQ5NjktYWVhMy1kMzlmYWU0" +
            "OWViMGRqdW5pdHlSb2xlc59qUk9MRV9VTklUWWpST0xFX0FETUlO/////w=="
        val properties = TokenDecrypterProperties(encryptionAlgorithm, encryptionKey)
        val decrypter = TokenDecrypter(properties)
        val exception = assertThrows<TokenDecryptionException> { decrypter.decrypt(token) }
        assertEquals(filterAuthenticationFilterTokenDataEmptySignature(), exception.message)
    }

    @Test
    fun `Decrypt security user with invalid signature`() {
        val token = "v2Z1c2VySWR4JGNiMDc5ZGVkLTI2NDQtNDk0YS1iYjc1LTlmNGQ4OTJmZWEwMWh1c2VyVHlwZWZDT01NT05oaXNBY3Rpdm" +
            "X1bm9yZ2FuaXphdGlvbklkeCQ5ZjhlZmI3OS1iMjA2LTQ0NTMtYjViNC00ZjQwYjM4NGFlY2Nxb3JnYW5pemF0aW9uUm9sZXOfa1JPTEV" +
            "fQ09NTU9Oa1JPTEVfU0VDT05E/29zZWN1cml0eVVuaXRpZXOfv2d1bml0eUlkeCRlMDliNThhYS1lN2YxLTQ5NjktYWVhMy1kMzlmYWU0" +
            "OWViMGRqdW5pdHlSb2xlc59qUk9MRV9VTklUWWpST0xFX0FETUlO/////w==.qwerty"
        val properties = TokenDecrypterProperties(encryptionAlgorithm, encryptionKey)
        val decrypter = TokenDecrypter(properties)
        val exception = assertThrows<TokenDecryptionException> { decrypter.decrypt(token) }
        assertEquals(filterAuthenticationFilterTokenDataInvalidSignature(), exception.message)
    }

}
