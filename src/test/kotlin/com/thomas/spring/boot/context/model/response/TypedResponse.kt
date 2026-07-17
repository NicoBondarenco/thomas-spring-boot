package com.thomas.spring.boot.context.model.response

import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.util.StringUtils.randomString
import java.util.UUID

data class TypedResponse<T>(
    val idTyped: UUID = randomUUIDv7(),
    val nameTyped: String = randomString(),
    val valueTyped: T,
)
