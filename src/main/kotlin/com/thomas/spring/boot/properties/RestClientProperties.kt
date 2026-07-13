package com.thomas.spring.boot.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.thomas.restclient")
data class RestClientProperties(
    val maxPoolConnections: Int,
    val maxRouteConnections: Int,
    val clientConnectionTimeout: Long,
    val clientReadTimeout: Int,
    val connectionIdleTimeout: Long,
    val timeToLive: Long,
    val requestConnectionTimeout: Long,
    val requestResponseTimeout: Long,
)
