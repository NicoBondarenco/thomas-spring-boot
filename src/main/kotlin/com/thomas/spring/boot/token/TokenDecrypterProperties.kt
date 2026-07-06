package com.thomas.spring.boot.token

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.jackson")
data class TokenDecrypterProperties(
    val encryptionAlgorithm: String,
    val encryptionKey: String,
)
