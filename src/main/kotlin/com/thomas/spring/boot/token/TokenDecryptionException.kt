package com.thomas.spring.boot.token

import com.thomas.core.exception.ApplicationException
import com.thomas.core.exception.ErrorType.UNAUTHORIZED_ACTION

class TokenDecryptionException(
    message: String
) : ApplicationException(
    message = message,
    type = UNAUTHORIZED_ACTION,
)
