package com.thomas.spring.boot.context.model.response

import java.util.UUID

data class InternalHeadersResponse(
    val unityId: UUID,
    val traceId: String,
    val authHeader: String,
)
