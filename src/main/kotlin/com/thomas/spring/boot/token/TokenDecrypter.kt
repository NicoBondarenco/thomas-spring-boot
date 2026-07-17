package com.thomas.spring.boot.token

import com.thomas.core.model.security.SecurityUser
import com.thomas.spring.boot.i18n.SpringMessageI18N.filterAuthenticationFilterTokenDataEmptySignature
import com.thomas.spring.boot.i18n.SpringMessageI18N.filterAuthenticationFilterTokenDataEmptyToken
import com.thomas.spring.boot.i18n.SpringMessageI18N.filterAuthenticationFilterTokenDataInvalidSignature
import com.thomas.spring.boot.properties.TokenDecrypterProperties
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

@OptIn(ExperimentalSerializationApi::class)
class TokenDecrypter(properties: TokenDecrypterProperties) {

    private val cbor = Cbor { ignoreUnknownKeys = true }
    private val key = SecretKeySpec(properties.encryptionKey.toByteArray(), properties.encryptionAlgorithm)
    private val hmac = Mac.getInstance(properties.encryptionAlgorithm).apply { init(key) }
    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()

    fun decrypt(token: String): SecurityUser {
        val (payload, signature) = decodeToken(token)
        validateSignature(payload, signature)
        return cbor.decodeFromByteArray<SecurityUser>(payload)
    }

    fun encrypt(user: SecurityUser): String {
        val payload = cbor.encodeToByteArray(user)
        val signature = hmac.doFinal(payload)
        return "${encoder.encodeToString(payload)}.${encoder.encodeToString(signature)}"
    }

    private fun decodeToken(token: String): Pair<ByteArray, ByteArray> {
        if (token.trim().isEmpty()) {
            throw TokenDecryptionException(filterAuthenticationFilterTokenDataEmptyToken())
        }
        val parts = token.split(".").map { it.trim() }
        if (parts.size != 2) {
            throw TokenDecryptionException(filterAuthenticationFilterTokenDataEmptySignature())
        }
        return decoder.decode(parts[0]) to decoder.decode(parts[1])
    }

    private fun validateSignature(payload: ByteArray, signature: ByteArray) {
        val expected = hmac.doFinal(payload)
        if (!MessageDigest.isEqual(expected, signature)) {
            throw TokenDecryptionException(filterAuthenticationFilterTokenDataInvalidSignature())
        }
    }

}
