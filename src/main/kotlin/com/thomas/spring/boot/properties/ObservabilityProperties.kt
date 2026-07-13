package com.thomas.spring.boot.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.thomas.observability")
data class ObservabilityProperties(
    val application: String,
    val instance: String,
)
