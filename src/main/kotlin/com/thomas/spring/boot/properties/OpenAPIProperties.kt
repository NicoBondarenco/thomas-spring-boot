package com.thomas.spring.boot.properties

import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.thomas.openapi.info")
data class OpenAPIInfoProperties(
    val title: String,
    val version: String,
    val description: String,
)

@ConfigurationProperties("spring.thomas.openapi.contact")
data class OpenAPIContactProperties(
    val name: String,
    val email: String,
    val url: String,
)

@ConfigurationProperties("spring.thomas.openapi.security")
data class OpenAPISecurityProperties(
    val name: String,
    val type: SecurityScheme.Type,
    val scheme: String,
    val format: String,
    val description: String,
)

@ConfigurationProperties("spring.thomas.openapi.group")
data class OpenAPIGroupProperties(
    val secured: String,
    val open: String,
)
