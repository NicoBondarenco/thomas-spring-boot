package com.thomas.spring.boot.model.resolver

import com.thomas.core.exception.ApplicationException
import com.thomas.core.exception.ErrorType.INVALID_PARAMETER
import com.thomas.spring.boot.i18n.SpringMessageI18N.errorExceptionMappingRequestParameterInvalidParameter

class RequestParameterException(
    parameter: String,
    value: String
) : ApplicationException(
    message = errorExceptionMappingRequestParameterInvalidParameter(parameter, value),
    type = INVALID_PARAMETER,
)
