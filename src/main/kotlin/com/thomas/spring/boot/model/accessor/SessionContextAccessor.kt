package com.thomas.spring.boot.model.accessor

import com.thomas.core.context.SessionContext
import com.thomas.core.context.SessionContextHolder
import io.micrometer.context.ThreadLocalAccessor
import org.slf4j.MDC

class SessionContextAccessor : ThreadLocalAccessor<SessionContext> {

    companion object {
        private const val TRACE_ID_KEY = "traceId"
        private const val SCOPE_ID_KEY = "scopeId"
    }

    override fun key(): String = SessionContext::class.java.name

    override fun getValue(): SessionContext = SessionContextHolder.context

    override fun setValue(value: SessionContext) {
        SessionContextHolder.context = value
        MDC.put(TRACE_ID_KEY, value.traceIdentifier)
        MDC.put(SCOPE_ID_KEY, value.scopeIdentifier)
    }

    override fun restore() {
        SessionContextHolder.clearContext()
        MDC.remove(TRACE_ID_KEY)
        MDC.remove(SCOPE_ID_KEY)
    }

    override fun setValue() = restore()

    override fun restore(previousValue: SessionContext) = setValue(previousValue)

}
