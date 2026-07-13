package com.thomas.spring.boot.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.thomas.webclient")
data class WebClientProperties(
    val clientConnectionTimeout: Long,
    val clientResponseTimeout: Long,
    val clientReadTimeout: Long,
    val clientWriteTimeout: Long,
    val clientPayloadSize: Int,
    val clientWiretapEnabled: Boolean,
    val connectionProviderName: String,
    val providerMaxConnections: Int,
    val pendingAcquireCount: Int,
    val pendingAcquireTimeout: Long,
    val maxIdleTime: Long,
)
