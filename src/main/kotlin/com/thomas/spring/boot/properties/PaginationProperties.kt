package com.thomas.spring.boot.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.thomas.pagination")
data class PaginationProperties(
    val defaultPageNumber: Long = 0,
    val defaultPageSize: Long = 10
)
