package com.thomas.spring.boot.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.thomas.webclient")
data class WebClientProperties(
    val clientConnectionTimeout: Long = 5000,
    val clientResponseTimeout: Long = 15000,
    val clientReadTimeout: Long = 15000,
    val clientWriteTimeout: Long = 15000,
    val clientPayloadSize: Int = (5 * 1024 * 1024),
    val clientWiretapEnabled: Boolean = true,
    val connectionProviderName: String = "default-webclient-connection-provider",
    val providerMaxConnections: Int = 100,
    val pendingAcquireCount: Int = 200,
    val pendingAcquireTimeout: Long = 5000,
    val maxIdleTime: Long = 30000,
)
