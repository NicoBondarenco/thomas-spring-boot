package com.thomas.spring.boot.context.model.response

import java.util.UUID

data class ContextResponse(
    val currentUser: UUID,
    val currentToken: String,
    val currentLocale: String,
    val currentUnity: UUID?,
    val withUser: String,
    val traceId: String,
)
