package com.thomas.spring.boot.model.accessor

import com.thomas.core.context.SessionContext
import com.thomas.core.context.SessionContextHolder
import io.micrometer.context.ThreadLocalAccessor

class SessionContextAccessor : ThreadLocalAccessor<SessionContext> {

    override fun key(): String = SessionContext::class.java.simpleName

    override fun getValue(): SessionContext = SessionContextHolder.context

    override fun setValue(value: SessionContext) {
        SessionContextHolder.context = value
    }

    override fun setValue() {
        SessionContextHolder.clearContext()
    }

    override fun restore(previousValue: SessionContext) {
        SessionContextHolder.context = previousValue
    }

    override fun restore() {
        SessionContextHolder.clearContext()
    }

}
