package com.thomas.spring.boot.context.model.response

import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.util.NumberUtils.randomInteger
import com.thomas.core.util.StringUtils.randomString
import java.util.UUID

data class SimpleResponse(
    val idSimple: UUID = randomUUIDv7(),
    val nameSimple: String = randomString(),
    val valueSimple: Int = randomInteger(),
)
