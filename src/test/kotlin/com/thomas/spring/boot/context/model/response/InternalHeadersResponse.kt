package com.thomas.spring.boot.context.model.response

import java.util.UUID

data class InternalHeadersResponse(
    val authHeader: String,
    val unityId: UUID,
    val traceId: String,
)
